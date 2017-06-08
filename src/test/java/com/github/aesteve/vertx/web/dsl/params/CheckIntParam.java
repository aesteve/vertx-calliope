package com.github.aesteve.vertx.web.dsl.params;


import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.asyncBool;

public class CheckIntParam extends TestBase {

    private final static String CHECK_MAP_INT_URL = "/tests/check/int/param/map";
    private final static String INT_PARAM_NAME = "theintparam";

    private final Function<Integer, AsyncResult<Integer>> lowerThan10 =
            asyncBool(i -> i < 10);

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(CHECK_MAP_INT_URL)
                .withParam(INT_PARAM_NAME, Integer.class).orFail(400)
                .check(lowerThan10)
                .orFail(402)
                .foldWithContext((i, rc) -> rc.response().end(i.toString()));
        return router;
    }

    @Test
    public void noParamShouldBe400(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_MAP_INT_URL, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void failedCheckShouldBe402(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_MAP_INT_URL + "?" + INT_PARAM_NAME + "=" + 11, resp -> {
            ctx.assertEquals(402, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void validParamShouldBe200(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_MAP_INT_URL + "?" + INT_PARAM_NAME + "=" + 9, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals("9", buffer.toString());
                async.complete();
            });
        });
    }

}
