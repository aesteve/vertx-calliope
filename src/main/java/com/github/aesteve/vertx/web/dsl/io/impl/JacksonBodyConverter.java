package com.github.aesteve.vertx.web.dsl.io.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import com.github.aesteve.vertx.web.dsl.io.StringBodyConverter;
import com.github.aesteve.vertx.web.dsl.io.exceptions.MarshallingException;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.io.IOException;

public class JacksonBodyConverter implements StringBodyConverter<Object> {

    private final ObjectMapper mapper = Json.mapper;

    @Override
    public <T extends Object> T fromString(String body, Class<T> clazz) throws MarshallingException {
        try {
            return mapper.readValue(body, clazz);
        } catch(IOException ioe) {
            throw new MarshallingException(ioe);
        }
    }

    @Override
    public <T> String toString(T payload) throws MarshallingException {
        try {
            return mapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new MarshallingException(e);
        }
    }

    @Override
    public void toResponseBody(RoutingContext context, HttpError error) {
        HttpServerResponse resp = context.response();
        resp.setStatusCode(error.status);
        resp.headers().addAll(error.additionalHeaders);
        if (error.message == null) {
            resp.end();
        } else {
            resp.end(
                    new JsonObject()
                            .put("error", error.status)
                            .put("message", error.message)
                            .toString()
            );
        }

    }
}
