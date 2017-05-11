package com.github.aesteve.vertx.web.dsl.io;

import com.github.aesteve.vertx.web.dsl.io.impl.JacksonWebMarshaller;
import com.github.aesteve.vertx.web.dsl.io.impl.PlainWebMarshaller;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

public interface WebMarshaller {

    <T> T fromRequestBody(RoutingContext context, Class<T> clazz);

    <T> void toResponseBody(RoutingContext context, T payload);
    <T> void toResponseBodyAsync(RoutingContext context, Future<T> payload);


    WebMarshaller PLAIN = new PlainWebMarshaller();
    WebMarshaller JSON = new JacksonWebMarshaller();
}
