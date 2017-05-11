package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.impl.WebRouterImpl;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static io.vertx.core.http.HttpMethod.*;

public interface WebRouter extends ErrorHandling<WebRouter>, Routing {

    static WebRouter router(Vertx vertx) {
        return new WebRouterImpl(vertx);
    }

    Router router();

    /* Global */
    WebRouter marshaller(String mime, BodyConverter marshaller);
    BodyConverter marshaller(RoutingContext context);

}
