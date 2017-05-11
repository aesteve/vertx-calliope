package com.github.aesteve.vertx.web.dsl.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.JSON;

public class TestAction extends TestBase {

    private final static String ACTION_URL = "/tests/actions/singleaction";

    private final static MockObject PAYLOAD = new MockObject();

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx)
                .marshaller("application/json", JSON);

        router.get(ACTION_URL)
                .action(rc -> PAYLOAD)
                .send();

        return router;
    }

    @Test
    public void testAction(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(ACTION_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                try {
                    ctx.assertEquals(Json.mapper.writeValueAsString(PAYLOAD), buffer.toString());
                } catch (JsonProcessingException e) {
                    ctx.fail(e);
                }
                async.complete();
            });
        });
    }



}
