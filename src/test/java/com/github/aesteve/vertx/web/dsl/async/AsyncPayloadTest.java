package com.github.aesteve.vertx.web.dsl.async;

import com.github.aesteve.vertx.web.dsl.ResponseBuilder;
import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.PLAIN;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.asyncBool;

public class AsyncPayloadTest extends TestBase {

    private final static String LIFT_ASYNC_URL = "/tests/lift/async";
    private final static String LIFT_ASYNC_SEND_URL = "/tests/lift/async/send";
    private final static String LIFT_ASYNC_MAPPED_URL = "/tests/lift/async/mapped";
    private final static String LIFT_ASYNC_FAILED_URL = "/tests/lift/async/failed";
    private final static String LIFT_ASYNC_CHECK_URL = "/tests/lift/async/check";

    private final static String PAYLOAD_1 = "lala";
    private final static String PAYLOAD_2 = "toto";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(LIFT_ASYNC_URL)
                .liftAsync(rc -> Future.succeededFuture(PAYLOAD_1))
                .fold();
        router.get(LIFT_ASYNC_SEND_URL)
                .marshaller("text/plain", PLAIN)
                .liftAsync(rc -> Future.succeededFuture(PAYLOAD_2))
                .fold();
        router.get(LIFT_ASYNC_MAPPED_URL)
                .liftAsync(rc -> Future.succeededFuture(new MockObject()))
                .flatMap(MockObject::getString)
                .foldWithContext((s, rc) -> rc.response().end(s));
        router.get(LIFT_ASYNC_FAILED_URL)
                .marshaller("text/plain", PLAIN)
                .withErrorDetails(true)
                .liftAsync(rc -> Future.failedFuture(new VertxException("Failed!!!")))
                .fold();
        router.get(LIFT_ASYNC_CHECK_URL)
                .marshaller("text/plain", PLAIN)
                .liftAsync(rc -> Future.succeededFuture(rc.request().getParam("test")))
                .check(asyncBool(s -> s.length() < 3)).orFail(402)
                .foldWithResponse((s, resp) -> resp.end("ok"));
        return router;
    }


    @Test
    public void shouldBeLifted(TestContext ctx) {
        testOk(LIFT_ASYNC_URL, PAYLOAD_1, ctx);
    }

    @Test
    public void shouldBeLiftedAndMapped(TestContext ctx) {
        testOk(LIFT_ASYNC_MAPPED_URL, new MockObject().getString(), ctx);
    }

    @Test
    public void shouldBeLiftedAndSent(TestContext ctx) {
        testOk(LIFT_ASYNC_SEND_URL, PAYLOAD_2, ctx);
    }

    @Test
    public void shouldBeFailedIfLiftFails(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(LIFT_ASYNC_FAILED_URL, resp -> {
            ctx.assertEquals(500, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertTrue(buffer.toString().contains("Failed!!!"));
                async.complete();
            });
        });
    }

    @Test
    public void shouldFailIfChecksFails(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(LIFT_ASYNC_CHECK_URL + "?test=aaaaaaaaaa", resp -> {
            ctx.assertEquals(402, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void shouldSucceedIfCheckSucceeds(TestContext ctx) {
        testOk(LIFT_ASYNC_CHECK_URL + "?test=a", "ok", ctx);
    }

    private void testOk(String url, String expectedString, TestContext ctx) {
        Async async = ctx.async();
        client().getNow(url, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(expectedString, buffer.toString());
                async.complete();
            });
        });

    }
}
