package com.github.aesteve.vertx.web.dsl.checking;

import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.fail;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.yield;

public class CheckHeaderTest extends TestingCheckBase {

    private final static String CHECK_HEADER_URL = "/tests/checking/header";
    private final static String CHECK_HEADER_URL_NONAME = "/tests/checking/header/noname";
    private final static String HEADER_NAME = "X-Custom-Header";
    private final static String CORRECT_HEADER = "expected_value";

    private final static Function<String, AsyncResult<String>> CHECK_HEADER = h -> CORRECT_HEADER.equals(h) ?
            yield(h) : fail(new VertxException("Invalid Header : " + h));

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(CHECK_HEADER_URL)
                .checkHeader(HEADER_NAME, "test", CHECK_HEADER).orFail(400)
                .handler(rc -> {
                    rc.response().end(rc.<String>get("test"));
                });
        router.get(CHECK_HEADER_URL_NONAME)
                .checkHeader(HEADER_NAME, CHECK_HEADER).orFail(400)
                .handler(rc -> {
                    rc.response().end(rc.<String>get(HEADER_NAME));
                });
        return router;
    }

    @Test
    public void noHeaderShouldBe400(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_HEADER_URL, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void invalidHeaderShouldBe400(TestContext ctx) {
        Async async = ctx.async();
        client().get(CHECK_HEADER_URL, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        }).putHeader(HEADER_NAME, "bad").end();
    }

    @Test
    public void correctHeaderShouldBe400(TestContext ctx) {
        testOk(CHECK_HEADER_URL, ctx);
    }

    @Test
    public void noHeaderNoNameShouldBe400(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_HEADER_URL_NONAME, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void invalidHeaderNoNameShouldBe400(TestContext ctx) {
        Async async = ctx.async();
        client().get(CHECK_HEADER_URL_NONAME, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        }).putHeader(HEADER_NAME, "bad").end();
    }

    @Test
    public void correctHeaderNoNameShouldBe400(TestContext ctx) {
        testOk(CHECK_HEADER_URL_NONAME, ctx);
    }


}
