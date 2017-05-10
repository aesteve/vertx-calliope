package com.github.aesteve.vertx.web.dsl.errors;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.RoutingContext;
import org.junit.Test;

import static io.vertx.core.http.HttpHeaders.ACCEPT;

public class ErrorsPlainTest extends TestBase {

    private final static String WITH_STACKTRACE_URL = "/tests/errors/withstack";
    private final static String WITHOUT_STACKTRACE_URL = "/tests/errors/withoutstack";
    private final static String CUSTOM_ERROR_HANDLER_URL = "/tests/errors/custom";
    private final static String ALL_GOOD = "Everything's fine";
    private final static VertxException FAILURE = new VertxException("Failed on purpose");
    private final static String GENERIC_ERROR_MSG = "failed :'(";

    private final static Handler<RoutingContext> failIfToldSo = rc -> {
        if (rc.get("fail")) {
            rc.fail(FAILURE);
            return;
        }
        rc.response().end("Everything's fine");
    };

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.withErrorDetails(true);
        router.get(WITH_STACKTRACE_URL)
                .boolParam("fail")
                .handler(failIfToldSo);
        router.get(WITHOUT_STACKTRACE_URL)
                .boolParam("fail")
                .withErrorDetails(false) // can be overriden
                .handler(failIfToldSo);
        router.get(CUSTOM_ERROR_HANDLER_URL)
                .boolParam("fail")
                .errorHandler(rc -> {
                    rc.response()
                        .setStatusCode(503)
                        .end(GENERIC_ERROR_MSG);
                })
                .handler(failIfToldSo);
        return router;
    }


    @Test
    public void testOkNoStackTrace(TestContext ctx) {
        testOk(WITHOUT_STACKTRACE_URL, ctx);
    }

    @Test
    public void testOkStackTrace(TestContext ctx) {
        testOk(WITH_STACKTRACE_URL, ctx);
    }

    @Test
    public void testFailureWithStack(TestContext ctx) {
        testFailure(WITH_STACKTRACE_URL, true, ctx);
    }

    @Test
    public void testFailureNoStack(TestContext ctx) {
        testFailure(WITHOUT_STACKTRACE_URL, false, ctx);
    }

    @Test
    public void testOkCustom(TestContext ctx) {
        testOk(CUSTOM_ERROR_HANDLER_URL, ctx);
    }

    @Test
    public void testCustomErrorHandler(TestContext ctx) {
        Async async = ctx.async();
        client().get(CUSTOM_ERROR_HANDLER_URL + "?fail=true", resp -> {
            ctx.assertEquals(503, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals(buff.toString(), GENERIC_ERROR_MSG);
                async.complete();
            });
        }).putHeader(ACCEPT, "text/plain").end();
    }

    private void testOk(String url, TestContext ctx) {
        Async async = ctx.async();
        client().getNow(url + "?fail=false", resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals(ALL_GOOD, buff.toString());
                async.complete();
            });
        });
    }

    private void testFailure(String url, boolean expectStack, TestContext ctx) {
        Async async = ctx.async();
        client().get(url + "?fail=true", resp -> {
            ctx.assertEquals(500, resp.statusCode());
            resp.bodyHandler(buff -> {
                isPlainErrorMessage(ctx, buff.toString(), expectStack);
                async.complete();
            });
        }).putHeader(ACCEPT, "text/plain").end();
    }

    private void isPlainErrorMessage(TestContext ctx, String s, boolean withStackTrace) {
        ctx.assertNotNull(s);
        boolean hasStackTrace = s.contains(this.getClass().getName());  // stacktrace
        if (withStackTrace) {
            ctx.assertTrue(s.contains("Failed on purpose")); // error message
            ctx.assertTrue(hasStackTrace);
        } else {
            ctx.assertFalse(hasStackTrace);
        }
    }


}
