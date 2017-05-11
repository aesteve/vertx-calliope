package com.github.aesteve.vertx.web.dsl.io.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.aesteve.vertx.web.dsl.io.exceptions.MarshallingException;
import com.github.aesteve.vertx.web.dsl.io.StringWebMarshaller;
import io.vertx.core.json.Json;

import java.io.IOException;

public class JacksonWebMarshaller implements StringWebMarshaller {

    private final ObjectMapper mapper = Json.mapper;

    @Override
    public <T> T fromString(String body, Class<T> clazz) throws MarshallingException {
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

}
