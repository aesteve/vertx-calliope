package com.github.aesteve.vertx.web.dsl.marshallers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.Date;

public class JacksonMarshallerTest extends TestBase {

    private final static String JSON_PATH = "/tests/marshallers/json";
    private final static String ASYNC_JSON_PATH = "/tests/marshallers/json/async";
    private final static Date NOW = new Date();

    final static class MockObject {
        final String string = "test";
        final int i = 3;

        public String getString() { return string; }
        public Date getDate() { return NOW; }
        public int getI() { return i; }
    }


    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.marshaller("application/json", WebMarshaller.JSON);
        router.get(JSON_PATH)
                .send(rc -> new MockObject());
        router.get(ASYNC_JSON_PATH)
                .sendFuture(rc -> Future.succeededFuture(new MockObject()));
        return router;
    }

    @Test
    public void mockObjectShouldBeMarshalled(TestContext ctx) {
        testOkMockObject(ctx, JSON_PATH);
    }

    @Test
    public void futureMockObjectShouldBeMarshalled(TestContext ctx) {
        testOkMockObject(ctx, ASYNC_JSON_PATH);
    }

    private void testOkMockObject(TestContext ctx, String path) {
        final Async async = ctx.async();
        client().getNow(path, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                try {
                    ctx.assertEquals(buff.toString(), Json.mapper.writeValueAsString(new MockObject()));
                } catch (JsonProcessingException e) {
                    ctx.fail(e);
                }
                async.complete();
            });
        });
    }
}
