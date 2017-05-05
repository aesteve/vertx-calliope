package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.impl.WebRouterImpl;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public interface WebRouter {

    static WebRouter router(Vertx vertx) {
        return new WebRouterImpl(vertx);
    }

    Router router();

    /* Global */
    WebRouter marshaller(String mime, WebMarshaller marshaller);

    WebMarshaller getMarshaller(RoutingContext context);

    /* Routing related */
    WebRoute route(String path);
    WebRoute route(String path, HttpMethod... methods);
    WebRoute get(String path);
    WebRoute post(String path);
    WebRoute put(String path);
    WebRoute patch(String path);
    WebRoute delete(String path);
    WebRoute options(String path);
    WebRoute trace(String path);
    WebRoute connect(String path);
    WebRoute head(String path);



}
