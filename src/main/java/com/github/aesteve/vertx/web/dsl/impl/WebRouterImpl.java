package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.WebRoute;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.ErrorHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.github.aesteve.vertx.web.dsl.utils.CollectionUtils.firstValue;
import static io.vertx.core.http.HttpMethod.*;

public class WebRouterImpl implements WebRouter {

    private final List<WebRouteImpl> routes = new ArrayList<>();
    private final LinkedHashMap<String, BodyConverter> marshallers = new LinkedHashMap<>();
    final Vertx vertx;
    final Router router;
    boolean displayErrorDetails;
    Handler<RoutingContext> errorHandler;

    public WebRouterImpl(Vertx vertx) {
        this.vertx = vertx;
        this.router = Router.router(vertx);
    }

    @Override
    public Router router() {
        routes.forEach(WebRouteImpl::attachHandlers);
        return router;
    }

    /* Global */
    @Override
    public WebRouter marshaller(String mime, BodyConverter marshaller) {
        marshallers.put(mime, marshaller);
        return this;
    }

    @Override
    public WebRouter withErrorDetails(boolean displayErrorDetails) {
        this.displayErrorDetails = displayErrorDetails;
        return this;
    }

    @Override
    public WebRouter errorHandler(Handler<RoutingContext> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    @Override
    public Handler<RoutingContext> errorHandler() {
        return errorHandler != null ? errorHandler : ErrorHandler.create(displayErrorDetails);
    }

    @Override
    public BodyConverter marshaller(RoutingContext context) {
        final String mime = context.getAcceptableContentType();
        if (mime != null) {
            return marshallers.get(context.getAcceptableContentType());
        } else {
            return firstValue(marshallers);
        }
    }


    /* Routing related */
    @Override
    public WebRoute route(String path) {
        final WebRouteImpl route = new WebRouteImpl(this, path);
        routes.add(route);
        return route;
    }

    @Override
    public WebRoute route(String path, HttpMethod... methods) {
        final WebRouteImpl route = new WebRouteImpl(this, path, methods);
        routes.add(route);
        return route;
    }

    @Override
    public WebRoute get(String path) {
        return route(path, GET);
    }

    @Override
    public WebRoute post(String path) {
        return route(path, POST);
    }

    @Override
    public WebRoute put(String path) {
        return route(path, PUT);
    }

    @Override
    public WebRoute patch(String path) {
        return route(path, HttpMethod.PATCH);
    }

    @Override
    public WebRoute delete(String path) {
        return route(path, DELETE);
    }

    @Override
    public WebRoute options(String path) {
        return route(path, OPTIONS);
    }

    @Override
    public WebRoute trace(String path) {
        return route(path, TRACE);
    }

    @Override
    public WebRoute connect(String path) {
        return route(path, CONNECT);
    }

    @Override
    public WebRoute head(String path) {
        return route(path, HEAD);
    }

}
