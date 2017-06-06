package com.github.aesteve.vertx.web.dsl.marshallers;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class NoUnmarshallerTest extends TestBase {

    private final static String NO_UNMARSHALLER_URL = "/tests/marshallers/nounmarshaller";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(NO_UNMARSHALLER_URL)
                .withErrorDetails(true)
                .consumes("text/plain")
                .withBody(MockObject.class)
                .foldWithContext((body, rc) -> rc.response().end());
        return router;
    }

    @Test
    public void testNoUnMarshaller(TestContext ctx) {
        Async async = ctx.async();
        client().get(NO_UNMARSHALLER_URL, resp -> {
            ctx.assertEquals(500, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertTrue(buffer.toString().contains("No unmarshaller found for text/plain"));
                async.complete();
            });
        }).putHeader(CONTENT_TYPE, "text/plain").end("something");
    }


}
