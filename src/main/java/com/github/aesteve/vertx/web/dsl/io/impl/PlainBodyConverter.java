package com.github.aesteve.vertx.web.dsl.io.impl;

import com.github.aesteve.vertx.web.dsl.io.StringBodyConverter;
import com.github.aesteve.vertx.web.dsl.io.exceptions.MarshallingException;
import io.vertx.core.VertxException;

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

}
