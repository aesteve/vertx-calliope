package com.github.aesteve.vertx.web.dsl.marshallers;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static io.vertx.core.http.HttpHeaders.ACCEPT;

public class NoMarshallerTest extends TestBase {

    private final static String NO_MARSHALLER_URL = "/tests/marshallers/nomarshaller";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(NO_MARSHALLER_URL)
                .withErrorDetails(true)
                .produces("text/plain")
                .lift(rc -> new MockObject())
                .fold();
        return router;
    }

    @Test
    public void testNoMarshaller(TestContext ctx) {
        Async async = ctx.async();
        client().get(NO_MARSHALLER_URL, resp -> {
            ctx.assertEquals(500, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertTrue(buffer.toString().contains("No marshaller found for text/plain"));
                async.complete();
            });
        }).putHeader(ACCEPT, "text/plain").end();
    }


}
