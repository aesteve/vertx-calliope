package com.github.aesteve.vertx.web.dsl.errors;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.RoutingContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.errors.HttpError.notFound;
import static io.vertx.core.http.HttpHeaders.ACCEPT;
import static io.vertx.core.http.HttpHeaders.CONTENT_LENGTH;

public class ErrorsPlainTest extends TestBase {

    private final static String WITH_STACKTRACE_URL = "/tests/errors/withstack";
    private final static String WITHOUT_STACKTRACE_URL = "/tests/errors/withoutstack";
    private final static String CUSTOM_ERROR_HANDLER_URL = "/tests/errors/custom";
    private final static String WITH_HTTP_ERROR_NO_MARSHALLER = "/tests/errors/status/nomarshaller";
    private final static String WITH_HTTP_ERROR_MARSHALLER = "/tests/errors/status/marshaller";
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
                .boolParam("fail").orFail(400)
                .handler(failIfToldSo);
        router.get(WITHOUT_STACKTRACE_URL)
                .boolParam("fail").orFail(400)
                .withErrorDetails(false) // can be overriden
                .handler(failIfToldSo);
        router.get(CUSTOM_ERROR_HANDLER_URL)
                .boolParam("fail").orFail(400)
                .onError(rc -> {
                    rc.response()
                        .setStatusCode(503)
                        .end(GENERIC_ERROR_MSG);
                })
                .handler(failIfToldSo);
        router.get(WITH_HTTP_ERROR_NO_MARSHALLER)
                .intParam("lala").orFail(notFound())
                .handler(rc -> rc.response().end("ok"));
        router.get(WITH_HTTP_ERROR_MARSHALLER)
                .marshaller("text/plain", BodyConverter.PLAIN)
                .intParam("lala").orFail(notFound())
                .handler(rc -> rc.response().end("ok"));
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

    @Test
    public void testHttpErrorInPlainTest(TestContext ctx) {
        Async async = ctx.async();
        client().get(WITH_HTTP_ERROR_NO_MARSHALLER, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            ctx.assertEquals("0", resp.getHeader(CONTENT_LENGTH));
            async.complete();
        }).putHeader(ACCEPT, "text/plain").end();
    }


    @Test
    public void testHttpErrorMarshalled(TestContext ctx) {
        Async async = ctx.async();
        client().get(WITH_HTTP_ERROR_NO_MARSHALLER, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            ctx.assertEquals("0", resp.getHeader(CONTENT_LENGTH));
            async.complete();
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
