package com.github.aesteve.vertx.web.dsl.errors;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class ErrorHandlerAtRouterLevel extends TestBase {

    private final static String ERROR_HANDLER_ROUTER_URL = "/tests/errors/handler/at/router/level";


    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.onError(rc -> rc.response().setStatusCode(500).end("Error !"));
        router.get(ERROR_HANDLER_ROUTER_URL)
                .handler(rc -> rc.fail(new VertxException("woops")));
        return router;
    }


    @Test
    public void testErrorHandlerAtRouterLevel(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(ERROR_HANDLER_ROUTER_URL, resp -> {
            ctx.assertEquals(500, resp.statusCode());
            async.complete();
        });
    }


}
