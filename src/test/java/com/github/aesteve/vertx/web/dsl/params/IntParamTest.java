package com.github.aesteve.vertx.web.dsl.params;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;
import static com.github.aesteve.vertx.web.dsl.errors.HttpError.BAD_REQUEST;

public class IntParamTest extends TestBase {


    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.get("/tests/int")
                .intParam("test").orFail(BAD_REQUEST)
                .handler(ctx -> {
                    final int test = ctx.get("test");
                    ctx.response().end("param = " + test);
                });
        return router;
    }

    @Test
    public void shouldBe400IfNoParam(TestContext ctx) {
        final Async async = ctx.async();
        client().getNow("/tests/int", resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void shouldBe400IfWrongParam(TestContext ctx) {
        final Async async = ctx.async();
        String param = "lala";
        client().getNow("/tests/int?test=" + param, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void shouldBe200IfParamSet(TestContext ctx) {
        final Async async = ctx.async();
        int param = 2;
        client().getNow("/tests/int?test=" + param, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(buffer.toString(), "param = " + param);
                async.complete();
            });
        });
    }

}
