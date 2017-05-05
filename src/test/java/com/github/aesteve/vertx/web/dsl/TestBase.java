package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public abstract class TestBase {

    protected final int PORT = 8888;
    protected final String HOST = "localhost";

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
}
