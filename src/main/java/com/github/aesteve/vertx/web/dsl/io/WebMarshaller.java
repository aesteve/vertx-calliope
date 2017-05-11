package com.github.aesteve.vertx.web.dsl.io;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface WebMarshaller {

    <T> void toResponseBody(RoutingContext context, T payload);
    <T> void toResponseBodyAsync(RoutingContext context, Future<T> payload);

}
