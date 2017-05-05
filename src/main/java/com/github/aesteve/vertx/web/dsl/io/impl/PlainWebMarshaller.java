package com.github.aesteve.vertx.web.dsl.io.impl;

import com.github.aesteve.vertx.web.dsl.io.StringWebMarshaller;
import com.github.aesteve.vertx.web.dsl.io.exceptions.MarshallingException;
import io.vertx.core.VertxException;

public class PlainWebMarshaller implements StringWebMarshaller {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T fromString(String body, Class<T> clazz) throws MarshallingException {
        try {
            return (T) body;
        } catch (ClassCastException cce) {
            throw new VertxException(clazz + " is not a String, cannot use PlainWebMarshaller");
        }
    }

    @Override
    public <T> String toString(T payload) throws MarshallingException {
        return payload.toString();
    }
}
