package com.github.aesteve.vertx.web.dsl.checking;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.io.WebMarshaller.PLAIN;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.fail;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.yield;

public class CheckingParamTest extends TestBase {

    private final static String CHECK_404_URL = "/tests/checks/404";

    private Function<String, AsyncResult<String>> correctParameter = val ->
            "correct".equals(val) ? yield(val) : fail(null);

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.marshaller("text/plain", PLAIN);
        router.get(CHECK_404_URL)
                .checkParam("test1", correctParameter, 404, "Param test1 not found")
                .send(rc -> "ok");
        return router;
    }

    @Test
    public void invalidParamShouldBe404(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_404_URL + "?test1=invalid", resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void correctParamShouldBeOK(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_404_URL + "?test1=correct", resp -> {
            ctx.assertEquals(200, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void noParamShouldBe404(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_404_URL, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        });
    }
}
