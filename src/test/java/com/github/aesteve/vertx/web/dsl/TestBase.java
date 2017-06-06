package com.github.aesteve.vertx.web.dsl;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.vertx.core.http.HttpHeaders.ACCEPT;

@RunWith(VertxUnitRunner.class)
public abstract class TestBase {

    protected final int PORT = 8888;
    protected final String HOST = "localhost";
    protected final static Date NOW = new Date();

    protected HttpServerOptions options = new HttpServerOptions()
            .setHost(HOST)
            .setPort(PORT);

    protected HttpClientOptions clientOptions = new HttpClientOptions()
            .setDefaultHost(HOST)
            .setDefaultPort(PORT);

    private Vertx vertx;

    @Before
    public void setup(TestContext ctx) {
        vertx = Vertx.vertx();
        final WebRouter router = createRouter(vertx);
        vertx.createHttpServer(options)
            .requestHandler(router.router()::accept)
            .listen(PORT, ctx.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext ctx) {
        vertx.close(ctx.asyncAssertSuccess());
    }

    protected abstract WebRouter createRouter(Vertx vertx);


    protected HttpClient client() {
        return vertx.createHttpClient(clientOptions);
    }


    public static class MockObject {
        private String string = "test";
        private int i = 3;
        private Date date = NOW;

        public String getString() { return string; }
        public void setString(String string) { this.string = string; }
        public Date getDate() { return date; }
        public void setDate(Date date) { this.date = date; }
        public int getI() { return i; }
        public void setI(int i) { this.i = i; }
    }

    protected void testJson(TestContext ctx, String url, MockObject mock) {
        testHttpStatusAndHeader(ctx, url, "application/json", 200, null, mock);
    }

    protected Map<CharSequence, String> expectHeader(CharSequence headerName, String headerValue) {
        Map<CharSequence, String> map = new HashMap<>();
        map.put(headerName, headerValue);
        return map;
    }

    protected void testHttpStatusAndHeader(
            TestContext ctx,
            String url,
            String contentType,
            int status,
            Map<CharSequence, String> expectedHeader,
            MockObject body) {
        Async async = ctx.async();
        client().get(url, resp -> {
            ctx.assertEquals(status, resp.statusCode());
            if (expectedHeader != null) {
                expectedHeader.forEach((k, v) -> {
                    ctx.assertEquals(resp.getHeader(k), v);
                });
            }
            if (body == null) {
                async.complete();
                return;
            }
            resp.bodyHandler(buffer -> {
                try {
                    ctx.assertEquals(Json.mapper.writeValueAsString(body), buffer.toString());
                    async.complete();
                } catch (JsonProcessingException e) {
                    ctx.fail(e);
                }
            });
        }).putHeader(ACCEPT, contentType).end();
    }

}
