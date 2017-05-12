package com.github.aesteve.vertx.web.dsl.io.impl;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import com.github.aesteve.vertx.web.dsl.io.StringBodyConverter;
import com.github.aesteve.vertx.web.dsl.io.exceptions.MarshallingException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class PlainBodyConverter implements StringBodyConverter<String> {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends String> T fromString(String body, Class<T> clazz) throws MarshallingException {
        return (T)body;
    }

    @Override
    public <T> String toString(T payload) throws MarshallingException {
        return payload.toString();
    }

    @Override
    public void toResponseBody(RoutingContext context, HttpError error) {
        HttpServerResponse resp = context.response();
        resp.setStatusCode(error.status);
        resp.headers().addAll(error.additionalHeaders);
        if (error.message != null) {
            resp.end(error.message);
        } else {
            resp.end();
        }
    }
}
