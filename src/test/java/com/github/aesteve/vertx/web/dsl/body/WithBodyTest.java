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

public class WithBodyTest extends TestBase {

    private final static String WITH_BODY_URL = "/tests/body";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        final WebRouter router = WebRouter.router(Vertx.vertx());
        router.marshaller("application/json", WebMarshaller.JSON);
        router.post(WITH_BODY_URL)
                .withBody("mock", MockObject.class)
                .send(rc -> rc.get("mock"));
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

}
