package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.impl.WebRouterImpl;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import com.github.aesteve.vertx.web.dsl.io.WebUnmarshaller;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public interface WebRouter extends ErrorHandling<WebRouter>, Routing {

    static WebRouter router(Vertx vertx) {
        return new WebRouterImpl(vertx);
    }

    Router router();

    /* Global */
    WebRouter converter(String mime, BodyConverter converter);
    WebRouter marshaller(String mime, WebMarshaller marshaller);
    WebRouter unmarshaller(String mime, WebUnmarshaller unmarshaller);

    WebMarshaller marshaller(RoutingContext context);
    WebUnmarshaller unmarshaller(RoutingContext context);

}
