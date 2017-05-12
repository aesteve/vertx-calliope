package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.CheckedWebRoute;
import com.github.aesteve.vertx.web.dsl.WebRoute;
import com.github.aesteve.vertx.web.dsl.WebRouteWithPayload;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import com.github.aesteve.vertx.web.dsl.io.PayloadSupplier;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import com.github.aesteve.vertx.web.dsl.io.WebUnmarshaller;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ErrorHandler;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;


public class WebRouteImpl implements WebRoute {

    final WebRouter parent;
    private final Router router;
    private final String path;
    private final Set<HttpMethod> methods = new HashSet<>();
    private final List<String> consumed = new ArrayList<>();
    private final List<String> produced = new ArrayList<>();
    private Handler<RoutingContext> errorHandler;

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
    public WebRoute marshaller(String mime, BodyConverter marshaller) {
        parent.converter(mime, marshaller);
        return this;
    }

    @Override
    public WebRoute handler(Handler<RoutingContext> handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public <T> WebRouteWithPayload<T> action(Function<RoutingContext, T> handler) {
        return new WebRouteWithPayloadImpl<>(this, handler);
    }

    @Override
    public <T> WebRouteWithPayload<T> withBody(Class<T> bodyClass) {
        return new WebRouteWithPayloadImpl<>(this, bodyClass);
    }

    @Override
    public <T> CheckedWebRoute check(String paramName, String ctxName, BiFunction<HttpServerRequest, String, String> extract, Function<String, AsyncResult<T>> checker) {
        return new CheckedWebRouteImpl<>(this, extract, checker, paramName, ctxName);
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
        } else if (result instanceof Future) {
            m.toResponseBodyAsync(rc, (Future<?>)result);
        } else {
            m.toResponseBody(rc, result);
        }
    }

    // Attach every handler to the routes
    void attachHandlers() {
        handlers.forEach(h -> {
            routes().forEach(r -> {
                r.failureHandler(errorHandler());
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
        final WebMarshaller m = parent.marshaller(rc);
        if (m == null) {
            rc.fail(new VertxException("No marshaller found for " + rc.getAcceptableContentType()));
            return;
        }
        handler.handle(m);
    }


    void withUnmarshaller(RoutingContext rc, Handler<WebUnmarshaller> handler) {
        final WebUnmarshaller m = parent.unmarshaller(rc);
        if (m == null) {
            rc.fail(new VertxException("No unmarshaller found for " + rc.request().getHeader(CONTENT_TYPE)));
            return;
        }
        handler.handle(m);
    }

    @Override
    public WebRoute withErrorDetails(boolean details) {
        errorHandler = ErrorHandler.create(details);
        return this;
    }

    @Override
    public WebRoute onError(Handler<RoutingContext> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public Handler<RoutingContext> errorHandler() {
        return errorHandler == null ? parent.errorHandler() : errorHandler;
    }
}
