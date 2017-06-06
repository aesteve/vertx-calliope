package com.github.aesteve.vertx.web.dsl.marshallers;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.PLAIN;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class UnmarshallerTest extends TestBase {

    private final static String UNMARSHALLER_URL = "/tests/unmarshaller/pure";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.unmarshaller("text/plain", PLAIN);
        router.withErrorDetails(true);
        router.get(UNMARSHALLER_URL)
                .withBody(String.class)
                .foldWithContext((sent, rc) -> {
                    rc.response().end(rc.getBodyAsString());
                }); // echo back
        return router;
    }

    @Test
    public void testBodyUnmarshalled(TestContext ctx) {
        Async async = ctx.async();
        String sent = "Something";
        client().get(UNMARSHALLER_URL, resp -> {
            // ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(sent, buffer.toString());
                async.complete();
            });
        }).putHeader(CONTENT_TYPE, "text/plain").end(sent);
    }
}
