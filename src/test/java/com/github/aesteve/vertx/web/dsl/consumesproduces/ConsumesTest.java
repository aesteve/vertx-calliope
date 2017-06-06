package com.github.aesteve.vertx.web.dsl.consumesproduces;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.PLAIN;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

public class ConsumesTest extends TestBase {

    private final static String PLAIN_TXT_URL = "/tests/consumes/text";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.post(PLAIN_TXT_URL)
                .consumes("text/plain", PLAIN)
                .withErrorDetails(true)
                .withBody(String.class)
                .fold();
        return router;
    }

    @Test
    public void ifNoContentTypeHeaderThen404(TestContext ctx) {
        final Async async = ctx.async();
        client().post(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        }).end("Test");
    }

    @Test
    public void withContentTypeHeaderShouldWork(TestContext ctx) {
        final Async async = ctx.async();
        String payload = "test";
        client().post(PLAIN_TXT_URL, resp -> {
            // ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(payload, buffer.toString());
                async.complete();
            });
        }).putHeader(CONTENT_TYPE, "text/plain").end(payload);
    }

    @Test
    public void wildCardShouldNotMatch(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        }).putHeader(HttpHeaders.CONTENT_TYPE, "*/*").end();
    }

    @Test
    public void invalidContentShouldBeA404(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        }).putHeader(HttpHeaders.CONTENT_TYPE, "application/json").end();
    }

}
