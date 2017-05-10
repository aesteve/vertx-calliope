package com.github.aesteve.vertx.web.dsl.marshallers;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.Optional;

import static com.github.aesteve.vertx.web.dsl.io.WebMarshaller.PLAIN;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.fail;

public class PlainMarshallerTest extends TestBase {

    private final static String PLAIN_TXT_URL = "/tests/marshallers/plain";
    private final static String PLAIN_TXT_URL_WITH_STATUS = "/tests/marshallers/plain/status";
    private final static String PLAIN_TXT_URL_NULL = "/tests/marshallers/plain/null";
    private final static String PLAIN_TXT_URL_OPTIONAL = "/tests/marshallers/plain/optional";
    private final static String PLAIN_TXT_URL_FAILED = "/tests/marshallers/plain/async/failed";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.marshaller("text/plain", PLAIN)
                .get(PLAIN_TXT_URL)
                .send(rc -> "hello !");
        router.get(PLAIN_TXT_URL_WITH_STATUS)
                .send(rc -> "hello !", 418);
        router.get(PLAIN_TXT_URL_NULL)
                .send(rc -> null);
        router.get(PLAIN_TXT_URL_OPTIONAL)
                .send(rc ->
                        Optional.ofNullable(rc.request().getParam("optional"))
                );
        router.get(PLAIN_TXT_URL_FAILED)
                .withErrorDetails(true)
                .sendFuture(rc -> fail(new VertxException("Sorry")));
        return router;
    }


    @Test
    public void plainTextShouldBeReturned(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals("hello !", buff.toString());
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end();
    }

    @Test
    public void plainTextShouldBeReturnedWithStatus(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL_WITH_STATUS, resp -> {
            ctx.assertEquals(418, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals("hello !", buff.toString());
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end();
    }

    @Test
    public void nullPayloadShouldEndUp404(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL_NULL, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end();
    }

    @Test
    public void absentOptionalPayloadShouldEndUp404(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL_OPTIONAL, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end();
    }


    @Test
    public void presentOptionalPayloadShouldBeReturned(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL_OPTIONAL + "?optional=test", resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
               ctx.assertEquals(buff.toString(), "test");
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end();
    }

    @Test
    public void futureFailureShouldBeReturned(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL_FAILED, resp -> {
            ctx.assertEquals(500, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertTrue(buff.toString().contains("Sorry"));
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end();
    }
}
