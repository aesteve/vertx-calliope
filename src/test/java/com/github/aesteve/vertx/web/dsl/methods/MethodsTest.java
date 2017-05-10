package com.github.aesteve.vertx.web.dsl.methods;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.web.RoutingContext;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.vertx.core.http.HttpMethod.*;

public class MethodsTest extends TestBase {

    private final static String ALL_ROUTES = "/tests/routes/allmethods";
    private final static String SINGLE_METHOD = "/tests/routes/single";
    private final static List<HttpMethod> withBodyMethods = Arrays.asList(GET, POST, PUT, PATCH, DELETE);

    private final static Handler<RoutingContext> echoMethod = rc -> {
        rc.response().end(rc.request().method().toString());

    };

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.route(ALL_ROUTES)
                .handler(rc -> rc.response().end("ALL"));
        router.get(SINGLE_METHOD)
                .handler(echoMethod);
        router.post(SINGLE_METHOD)
                .handler(echoMethod);
        router.put(SINGLE_METHOD)
                .handler(echoMethod);
        router.patch(SINGLE_METHOD)
                .handler(echoMethod);
        router.delete(SINGLE_METHOD)
                .handler(echoMethod);
        router.options(SINGLE_METHOD)
                .handler(echoMethod);
        router.head(SINGLE_METHOD)
                .handler(echoMethod);
        router.trace(SINGLE_METHOD)
                .handler(echoMethod);
        router.connect(SINGLE_METHOD)
                .handler(echoMethod);

        return router;
    }

    @Test
    public void allRoutesMatchGet(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.GET, "ALL", ctx);
    }

    @Test
    public void allRoutesMatchPost(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.POST, "ALL", ctx);
    }

    @Test
    public void allRoutesMatchPut(TestContext ctx) {
        testRoute(ALL_ROUTES, PUT, "ALL", ctx);
    }

    @Test
    public void allRoutesMatchPatch(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.PATCH, "ALL", ctx);
    }

    @Test
    public void allRoutesMatchDelete(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.DELETE, "ALL", ctx);
    }

    @Test
    public void allRoutesMatchOptions(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.OPTIONS, "ALL", ctx);
    }

    @Test
    public void allRoutesMatchHead(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.HEAD, null, ctx);
    }

    @Test
    public void allRoutesMatchTrace(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.TRACE, null, ctx);
    }

    @Test
    public void allRoutesMatchConnect(TestContext ctx) {
        testRoute(ALL_ROUTES, HttpMethod.CONNECT, null, ctx);
    }

    @Test
    public void singleRouteMatchGet(TestContext ctx) {
        testSingleRoute(GET, ctx);
    }

    @Test
    public void singleRouteMatchPost(TestContext ctx) {
        testSingleRoute(POST, ctx);
    }

    @Test
    public void singleRouteMatchPatch(TestContext ctx) {
        testSingleRoute(PATCH, ctx);
    }

    @Test
    public void singleRouteMatchPut(TestContext ctx) {
        testSingleRoute(PUT, ctx);
    }

    @Test
    public void singleRouteMatchDelete(TestContext ctx) {
        testSingleRoute(DELETE, ctx);
    }

    @Test
    public void singleRouteMatchOptions(TestContext ctx) {
        testSingleRoute(OPTIONS, ctx);
    }

    @Test
    public void singleRouteMatchTrace(TestContext ctx) {
        testSingleRoute(TRACE, ctx);
    }

    @Test
    public void singleRouteMatchConnect(TestContext ctx) {
        testSingleRoute(CONNECT, ctx);
    }

    @Test
    public void singleRouteMatchHead(TestContext ctx) {
        testSingleRoute(HEAD, ctx);
    }

    private void testSingleRoute(HttpMethod method, TestContext ctx) {
        testRoute(SINGLE_METHOD, method, withBodyMethods.contains(method) ? method.toString() : null, ctx);
    }

    private void testRoute(String path, HttpMethod method, String expected, TestContext ctx) {
        Async async = ctx.async();
        client().request(method, path, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            if (expected == null) {
                async.complete();
                return;
            }
            resp.bodyHandler(buff -> {
                ctx.assertEquals(expected, buff.toString());
                async.complete();
            });
        }).end();
    }
}
