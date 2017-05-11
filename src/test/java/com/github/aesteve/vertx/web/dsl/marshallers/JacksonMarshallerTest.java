package com.github.aesteve.vertx.web.dsl.marshallers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.fail;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.yield;

public class JacksonMarshallerTest extends TestBase {

    private final static String JSON_PATH = "/tests/marshallers/json";
    private final static String ASYNC_JSON_PATH = "/tests/marshallers/json/async";
    private final static String ASYNC_JSON_PATH_FAILURE = "/tests/marshallers/json/async/failed";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.converter("application/json", BodyConverter.JSON);
        router.withErrorDetails(true);
        router.get(JSON_PATH)
                .send(rc -> new MockObject());
        router.get(ASYNC_JSON_PATH)
                .send(rc -> yield(new MockObject()));
        router.get(ASYNC_JSON_PATH_FAILURE)
                .send(rc -> fail(new VertxException("Sorry")));
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
