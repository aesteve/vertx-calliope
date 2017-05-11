package com.github.aesteve.vertx.web.dsl.checking;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.async;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.fail;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.yield;

public class PureCheckTest extends TestBase {

    private final static String CHECK_URL = "/tests/checking/raw";
    private final static String HEADER_NAME = "X-Custom-Header";
    private final static String CORRECT_HEADER = "expected_value";

    private final static Function<String, AsyncResult<String>> CHECK_HEADER = h -> CORRECT_HEADER.equals(h) ?
            yield(h) : fail(new VertxException("Invalid Header : " + h));

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(CHECK_URL)
                .check(HEADER_NAME, "test", HttpServerRequest::getHeader, CHECK_HEADER, 501, "Invalid Header")
                .handler(rc -> {
                    rc.response().end(rc.<String>get("test"));
                });
        return router;
    }

    @Test
    public void noHeaderShouldBe501(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(CHECK_URL , resp -> {
            ctx.assertEquals(501, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void invalidHeaderShouldBe501(TestContext ctx) {
        Async async = ctx.async();
        client().get(CHECK_URL , resp -> {
            ctx.assertEquals(501, resp.statusCode());
            async.complete();
        }).putHeader(HEADER_NAME, "bad").end();
    }

    @Test
    public void validHeaderShouldBe501(TestContext ctx) {
        Async async = ctx.async();
        client().get(CHECK_URL , resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(CORRECT_HEADER, buffer.toString());
                async.complete();
            });
        }).putHeader(HEADER_NAME, CORRECT_HEADER).end();
    }
}
