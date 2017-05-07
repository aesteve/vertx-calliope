package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.WebRoute;
import com.github.aesteve.vertx.web.dsl.WebRouteWithBody;
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


    WebRouteImpl(WebRouteImpl parent) {
        this.parent = parent.parent;
        this.router = parent.router;
        this.path = parent.path;
        methods.addAll(parent.methods);
        consumed.addAll(parent.consumed);
        produced.addAll(parent.produced);
    }

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
    public <T> WebRouteWithBody<T> withBody(Class<T> bodyClass) {
        return new WebRouteWithBodyImpl<>(this, bodyClass);
    }

    @Override
    public <T> void send(PayloadSupplier<T> supplier, int statusCode) {
        handler(rc -> {
            withMarshaller(rc, m -> {
                rc.response().setStatusCode(statusCode);
                marshall(m, rc, supplier.getPayload(rc));
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
                    marshall(m, rc, res.result());
                });
            });
        });
    }

    private <T> void marshall(WebMarshaller m, RoutingContext rc, T result) {
        if (result == null) {
            rc.response().setStatusCode(404).end();
            return;
        }
        if (result instanceof Optional) {
            final Optional res = (Optional)result;
            if (res.isPresent()) {
                m.toResponseBody(rc, res.get());
            } else {
                rc.response().setStatusCode(404).end();
            }
        } else {
            m.toResponseBody(rc, result);
        }
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

    void withMarshaller(RoutingContext rc, Handler<WebMarshaller> handler) {
        final WebMarshaller m = parent.getMarshaller(rc);
        if (m == null) {
            rc.fail(new VertxException("No marshaller found for " + rc.getAcceptableContentType()));
            return;
        }
        handler.handle(m);
    }
}
