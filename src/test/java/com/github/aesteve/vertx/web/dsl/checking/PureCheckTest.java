package com.github.aesteve.vertx.web.dsl.checking;

import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.fail;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.yield;

public class PureCheckTest extends TestingCheckBase {

    private final static String CHECK_URL = "/tests/checking/raw";
    private final static String CHECK_URL_CUSTOM = "/tests/checking/raw/custom";

    private final static Function<String, AsyncResult<String>> CHECK_HEADER = h -> CORRECT_HEADER.equals(h) ?
            yield(h) : fail(new VertxException("Invalid Header : " + h));

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(CHECK_URL)
                .check(HEADER_NAME, "test", HttpServerRequest::getHeader, CHECK_HEADER)
                .handler(rc -> {
                    rc.response().end(rc.<String>get("test"));
                });
        router.get(CHECK_URL_CUSTOM)
                .check(HEADER_NAME, HttpServerRequest::getHeader, CHECK_HEADER, 501, "Not implemented yet")
                .handler(rc -> {
                    rc.response().end(rc.<String>get(HEADER_NAME));
                });
        return router;
    }

    @Test
    public void noHeaderShouldBe400(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_URL , resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void invalidHeaderShouldBe400(TestContext ctx) {
        Async async = ctx.async();
        client().get(CHECK_URL , resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        }).putHeader(HEADER_NAME, "bad").end();
    }

    @Test
    public void validHeaderShouldBeOk(TestContext ctx) {
        testOk(CHECK_URL, ctx);
    }

    @Test
    public void noHeaderShouldBe501(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_URL_CUSTOM, resp -> {
            ctx.assertEquals(501, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void invalidHeaderShouldBe501(TestContext ctx) {
        Async async = ctx.async();
        client().get(CHECK_URL_CUSTOM , resp -> {
            ctx.assertEquals(501, resp.statusCode());
            async.complete();
        }).putHeader(HEADER_NAME, "bad").end();
    }

    @Test
    public void validHeaderShouldBeOkEventWithCustomCode(TestContext ctx) {
        testOk(CHECK_URL_CUSTOM, ctx);
    }

}
