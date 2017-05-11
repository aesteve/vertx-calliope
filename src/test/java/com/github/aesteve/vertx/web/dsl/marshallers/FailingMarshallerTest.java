package com.github.aesteve.vertx.web.dsl.marshallers;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.StringWebMarshaller;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import com.github.aesteve.vertx.web.dsl.io.exceptions.MarshallingException;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;
import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;


public class FailingMarshallerTest extends TestBase {

    private final static String FAILING_MARSHALLER_READ_URL = "/tests/marshallers/failing/read";
    private final static String FAILING_MARSHALLER_WRITE_URL = "/tests/marshallers/failing/write";

    private WebMarshaller failingMarshaller = new StringWebMarshaller() {
        @Override
        public <T> T fromString(String body, Class<T> clazz) throws MarshallingException {
            throw new MarshallingException("Can't read");
        }

        @Override
        public <T> String toString(T payload) throws MarshallingException {
            throw new MarshallingException("Can't write");
        }
    };

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(FAILING_MARSHALLER_READ_URL)
                .withErrorDetails(true)
                .consumes("text/plain", failingMarshaller)
                .withBody(MockObject.class)
                .send();
        router.get(FAILING_MARSHALLER_WRITE_URL)
                .withErrorDetails(true)
                .produces("text/plain", failingMarshaller)
                .send(rc -> new MockObject());
        return router;
    }

    @Test
    public void testReadErrorIsMarshalled(TestContext ctx) {
        client()
                .get(FAILING_MARSHALLER_READ_URL, assertError("Can't read", ctx))
                .putHeader(CONTENT_TYPE, "text/plain")
                .end("Bleh");
    }

    @Test
    public void testWriteErrorIsMarshalled(TestContext ctx) {
        client()
                .get(FAILING_MARSHALLER_WRITE_URL, assertError("Can't write", ctx))
                .putHeader(ACCEPT, "text/plain")
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
