package com.github.aesteve.vertx.web.dsl.marshallers;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.WebMarshaller.PLAIN;

public class PlainMarshallerTest extends TestBase {

    private final static String PLAIN_TXT_URL = "/tests/marshallers/plain";
    private final static String PLAIN_TXT_URL_WITH_STATUS = "/tests/marshallers/plain/status";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(vertx);
        router.marshaller("text/plain", PLAIN)
                .get(PLAIN_TXT_URL)
                .send(rc -> "hello !");
        router.marshaller("text/plain", PLAIN)
                .get(PLAIN_TXT_URL_WITH_STATUS)
                .send(rc -> "hello !", 418);
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
}
