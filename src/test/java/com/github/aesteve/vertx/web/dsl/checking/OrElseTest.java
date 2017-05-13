package com.github.aesteve.vertx.web.dsl.checking;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class OrElseTest extends TestBase {

    private final static String OR_ELSE_URL = "/tests/or/else";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(OR_ELSE_URL)
                .intParam("age")
                .orElse(42)
                .fold((age, rc) -> rc.response().end(age.toString()));
        return router;
    }

    @Test
    public void testDefaultValue(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(OR_ELSE_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals("42", buffer.toString());
                async.complete();
            });
        });
    }

    @Test
    public void testRealValue(TestContext ctx) {
        Async async = ctx.async();
        Integer age = 24;
        client().getNow(OR_ELSE_URL + "?age=" + age, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(age.toString(), buffer.toString());
                async.complete();
            });
        });
    }
}
