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

    private final static MockObject MOCK_1 = new MockObject();
    private final static MockObject MOCK_2 = new MockObject();

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.converter("application/json", BodyConverter.JSON);
        router.withErrorDetails(true);
        router.get(JSON_PATH)
                .lift(rc -> MOCK_1)
                .fold();
        router.get(ASYNC_JSON_PATH)
                .liftAsync(rc -> yield(MOCK_2))
                .fold();
        router.get(ASYNC_JSON_PATH_FAILURE)
                .liftAsync(rc -> fail(new VertxException("Sorry")))
                .fold();
        return router;
    }

    @Test
    public void mockObjectShouldBeMarshalled(TestContext ctx) {
        testJson(ctx, JSON_PATH, MOCK_1);
    }

    @Test
    public void futureMockObjectShouldBeMarshalled(TestContext ctx) {
        testJson(ctx, ASYNC_JSON_PATH, MOCK_2);
    }

}
