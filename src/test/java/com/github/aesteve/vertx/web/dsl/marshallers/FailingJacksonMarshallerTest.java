package com.github.aesteve.vertx.web.dsl.marshallers;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.JSON;
import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;


public class FailingJacksonMarshallerTest extends TestBase {

    private final static String FAILING_JACKSON_READ_URL = "/tests/marshallers/jackson/failing/read";
    private final static String FAILING_JACKSON_WRITE_URL = "/tests/marshallers/jackson/failing/write";
    private final String jsonMime = "application/json";

    private class MockFailingObject extends MockObject {
        @Override
        public String getString() {
            throw new RuntimeException("Error during marshalling");
        }
    }

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(FAILING_JACKSON_READ_URL)
                .withErrorDetails(true)
                .consumes(jsonMime, JSON)
                .withBody(MockObject.class)
                .send();
        router.get(FAILING_JACKSON_WRITE_URL)
                .withErrorDetails(true)
                .produces(jsonMime, JSON)
                .perform(rc -> new MockFailingObject())
                .send();
        return router;
    }

    @Test
    public void testReadErrorIsMarshalled(TestContext ctx) {
        client()
                .get(FAILING_JACKSON_READ_URL, assertError("Unrecognized token", ctx))
                .putHeader(CONTENT_TYPE, jsonMime)
                .end("Bleh");
    }

    @Test
    public void testWriteErrorIsMarshalled(TestContext ctx) {
        client()
                .get(FAILING_JACKSON_WRITE_URL, assertError("Error during marshalling", ctx))
                .putHeader(ACCEPT, jsonMime)
                .end();
    }

    private Handler<HttpClientResponse> assertError(String message, TestContext ctx) {
        Async async = ctx.async();
        return resp -> {
            ctx.assertEquals(500, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertTrue(buffer.toString().contains(message));
                async.complete();
            });
        };
    }
}
