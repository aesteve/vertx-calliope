package com.github.aesteve.vertx.web.dsl.consumesproduces;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

public class ProducesTest extends TestBase {

    private final static String PLAIN_TXT_URL = "/tests/consumes/text";


    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.get(PLAIN_TXT_URL)
            .produces("text/plain")
            .handler(rc -> {
                final String mime = rc.getAcceptableContentType();
                rc.response().end(mime == null ? "null" : mime);
            });
        return router;
    }

    @Test
    public void ifNoAcceptHeaderThenAcceptableIsNull(TestContext ctx) {
        final Async async = ctx.async();
        client().getNow(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals("null", buff.toString());
                async.complete();
            });
        });
    }

    @Test
    public void acceptHeaderShouldBeMatched(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals("text/plain", buff.toString());
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "text/plain").end();
    }

    @Test
    public void wildCardShouldMatch(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals("text/plain", buff.toString());
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end();
    }

    @Test
    public void invalidAcceptShouldBeA404(TestContext ctx) {
        final Async async = ctx.async();
        client().get(PLAIN_TXT_URL, resp -> {
            ctx.assertEquals(404, resp.statusCode());
            async.complete();
        }).putHeader(HttpHeaders.ACCEPT, "application/json").end();
    }


}
