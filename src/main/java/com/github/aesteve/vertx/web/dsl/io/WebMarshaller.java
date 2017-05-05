package com.github.aesteve.vertx.web.dsl.io;

import com.github.aesteve.vertx.web.dsl.io.impl.JacksonWebMarshaller;
import com.github.aesteve.vertx.web.dsl.io.impl.PlainWebMarshaller;
import io.vertx.ext.web.RoutingContext;

public interface WebMarshaller {

    <T> T fromRequestBody(RoutingContext context, Class<T> clazz);

    <T> void toResponseBody(RoutingContext context, T payload);


    static WebMarshaller plain() { return new PlainWebMarshaller(); }
    static WebMarshaller json() { return new JacksonWebMarshaller(); }
}
