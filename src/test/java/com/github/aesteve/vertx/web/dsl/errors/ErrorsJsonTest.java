package com.github.aesteve.vertx.web.dsl.errors;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.errors.HttpError.BAD_REQUEST;
import static com.github.aesteve.vertx.web.dsl.errors.HttpError.badRequest;
import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.JSON;
import static io.vertx.core.http.HttpHeaders.CONTENT_LENGTH;

public class ErrorsJsonTest extends TestBase {

    private final static String JSON_ERROR_MESSAGE_URL = "/tests/marshallers/json/error/withmessage";
    private final static String JSON_ERROR_NOMESSAGE_URL = "/tests/marshallers/json/error/withoutmessage";
    private final static String DOESNT_EXIST = "Sorry, does not exist";


    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.marshaller("application/json", JSON);
        router.get(JSON_ERROR_MESSAGE_URL)
                .intParam("present").orFail(badRequest(DOESNT_EXIST))
                .foldWithContext((present, rc) -> rc.response().end("ok"));
        router.get(JSON_ERROR_NOMESSAGE_URL)
                .intParam("present").orFail(BAD_REQUEST)
                .foldWithContext((present, rc) -> rc.response().end("ok"));
        return router;
    }

    @Test
    public void testJsonHttpError(TestContext ctx) {
        Async async = ctx.async();
        JsonObject expectedJSON = new JsonObject()
                .put("error", 400)
                .put("message", DOESNT_EXIST);
        client().getNow(JSON_ERROR_MESSAGE_URL, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(expectedJSON, buffer.toJsonObject());
                async.complete();
            });
        });
    }

    @Test
    public void testJsonHttpErrorNoMessage(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(JSON_ERROR_NOMESSAGE_URL, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            ctx.assertEquals("0", resp.getHeader(CONTENT_LENGTH));
            async.complete();
        });
    }
}
