package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.WebRoute;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.AsyncPayloadSupplier;
import com.github.aesteve.vertx.web.dsl.io.PayloadSupplier;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.*;
import java.util.stream.Stream;

public class WebRouteImpl implements WebRoute {

    private final WebRouter parent;
    private final Router router;
    private final String path;
    private final Set<HttpMethod> methods = new HashSet<>();
    private final List<String> consumed = new ArrayList<>();
    private final List<String> produced = new ArrayList<>();

    protected final List<Handler<RoutingContext>> handlers = new ArrayList<>();


    WebRouteImpl(WebRouterImpl parent, String path, HttpMethod... methods) {
        this.parent = parent;
        this.router = parent.router;
        this.path = path;
        if (methods != null) {
            Collections.addAll(this.methods, methods);
        }
    }

    @Override
    public WebRoute consumes(String mime) {
        consumed.add(mime);
        return this;
    }

    @Override
    public WebRoute produces(String mime) {
        produced.add(mime);
        return this;
    }

    @Override
    public WebRoute handler(Handler<RoutingContext> handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public <T> WebRoute withBody(String name, Class<T> bodyClass) {
        handler(BodyHandler.create());
        return handler(rc -> {
            withMarshaller(rc, m -> {
                rc.put(name, m.fromRequestBody(rc, bodyClass));
                rc.next();
            });
        });
    }

    @Override
    public <T> void send(PayloadSupplier<T> supplier, int statusCode) {
        handler(rc -> {
            withMarshaller(rc, m -> {
                rc.response().setStatusCode(statusCode);
                m.toResponseBody(rc, supplier.getPayload(rc));
            });
        });
    }

    @Override
    public <T> void sendFuture(AsyncPayloadSupplier<T> supplier, int statusCode) {
        handler(rc -> {
            withMarshaller(rc, m -> {
                supplier.getPayload(rc).setHandler(res -> {
                    if (res.failed()) {
                        rc.fail(res.cause());
                        return;
                    }
                    rc.response().setStatusCode(statusCode);
                    m.toResponseBody(rc, res.result());
                });
            });
        });
    }

    // Attach every handler to the routes
    void attachHandlers() {
        handlers.forEach(h -> {
            routes().forEach(r -> {
                r.handler(h);
            });
        });
    }

    // Creates all the routes
    private Stream<Route> routes() {
        Stream<Route> routes;
        if (methods.isEmpty()) {
            routes = Stream.of(router.route(path));
        } else {
            routes = methods.stream().map(m -> router.route(m, path));
        }
        if (!consumed.isEmpty()) {
            routes = routes.flatMap(r -> consumed.stream().map(r::consumes));
        }
        if (!produced.isEmpty()) {
            routes = routes.flatMap(r -> produced.stream().map(r::produces));
        }
        return routes;
    }

    private void withMarshaller(RoutingContext rc, Handler<WebMarshaller> handler) {
        final WebMarshaller m = parent.getMarshaller(rc);
        if (m == null) {
            rc.fail(new VertxException("No marshaller found for " + rc.getAcceptableContentType()));
            return;
        }
        handler.handle(m);
    }
}
