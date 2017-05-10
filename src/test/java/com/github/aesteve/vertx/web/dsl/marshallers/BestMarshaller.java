package com.github.aesteve.vertx.web.dsl.marshallers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.WebMarshaller.JSON;
import static com.github.aesteve.vertx.web.dsl.io.WebMarshaller.PLAIN;
import static io.vertx.core.http.HttpHeaders.ACCEPT;

public class BestMarshaller extends TestBase {

    private final static String BEST_MARSHALLER_URL = "/tests/marshallers/best";
    private final MockObject mock = new MockObject();

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.marshaller("application/json", JSON);
        router.marshaller("text/plain", PLAIN);
        router.get(BEST_MARSHALLER_URL)
                .produces("application/json")
                .produces("text/plain")
                .send(rc -> mock);
        return router;
    }

    @Test
    public void testJson(TestContext ctx) {
        Async async = ctx.async();
        client().get(BEST_MARSHALLER_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                try {
                    ctx.assertEquals(Json.mapper.writeValueAsString(mock), buffer.toString());
                    async.complete();
                } catch (JsonProcessingException e) {
                    ctx.fail(e);
                }
            });
        }).putHeader(ACCEPT, "application/json").end();
    }

    @Test
    public void testText(TestContext ctx) {
        Async async = ctx.async();
        client().get(BEST_MARSHALLER_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(mock.toString(), buffer.toString());
                async.complete();
            });
        }).putHeader(ACCEPT, "text/plain").end();
    }

}
