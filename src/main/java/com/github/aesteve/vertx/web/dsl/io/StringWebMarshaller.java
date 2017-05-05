package com.github.aesteve.vertx.web.dsl.io;

import com.github.aesteve.vertx.web.dsl.io.exceptions.MarshallingException;
import io.vertx.ext.web.RoutingContext;

public interface StringWebMarshaller extends WebMarshaller {

    <T> T fromString(String body, Class<T> clazz) throws MarshallingException;

    <T> String toString(T payload) throws MarshallingException;

    @Override
    default <T> T fromRequestBody(RoutingContext context, Class<T> clazz) {
        try {
            return fromString(context.getBodyAsString(), clazz);
        } catch (MarshallingException mse) {
            context.fail(mse);
            return null;
        }
    }

    @Override
    default <T> void toResponseBody(RoutingContext context, T payload) {
        try {
            context.response().end(toString(payload));
        } catch (MarshallingException mse) {
            context.fail(mse);
        }
    }

}
