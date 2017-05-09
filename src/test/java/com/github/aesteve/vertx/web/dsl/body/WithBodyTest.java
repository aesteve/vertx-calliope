package com.github.aesteve.vertx.web.dsl.body;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.function.Function;

public class WithBodyTest extends TestBase {

    private final static String WITH_BODY_URL = "/tests/body";
    private final static String WITH_BODY_MAP_URL = "/tests/body/map";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(Vertx.vertx());
        router.marshaller("application/json", WebMarshaller.JSON);
        router.post(WITH_BODY_URL)
                .withBody(MockObject.class)
                .send(Function.identity());
        router.post(WITH_BODY_MAP_URL)
                .withBody(MockObject.class)
                .map(MockObject::getDate)
                .send(Function.identity());
        return router;
    }

    @Test
    public void bodyShouldBeReturned(TestContext ctx) throws Exception {
        final Async async = ctx.async();
        final String sent = Json.mapper.writeValueAsString(new MockObject());
        client().post(WITH_BODY_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals(sent, buff.toString());
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end(sent);
    }

    @Test
    public void bodyShouldBeMapped(TestContext ctx) throws Exception {
        final Async async = ctx.async();
        final MockObject mock = new MockObject();
        final String expected = Json.mapper.writeValueAsString(mock.getDate());
        final String sent = Json.mapper.writeValueAsString(mock);
        client().post(WITH_BODY_MAP_URL, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buff -> {
                ctx.assertEquals(expected, buff.toString());
                async.complete();
            });
        }).putHeader(HttpHeaders.ACCEPT, "*/*").end(sent);
    }
}
