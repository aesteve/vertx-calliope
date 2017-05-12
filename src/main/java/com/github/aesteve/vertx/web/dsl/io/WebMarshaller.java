package com.github.aesteve.vertx.web.dsl.io;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface WebMarshaller {

    void toResponseBody(RoutingContext context, HttpError payload);
    <T> void toResponseBody(RoutingContext context, T payload);
    <T> void toResponseBodyAsync(RoutingContext context, Future<T> payload);

}
