package com.github.aesteve.vertx.web.dsl.io;

import com.github.aesteve.vertx.web.dsl.io.impl.JacksonBodyConverter;
import com.github.aesteve.vertx.web.dsl.io.impl.PlainBodyConverter;

public interface BodyConverter<T> extends WebMarshaller, WebUnmarshaller<T> {

    BodyConverter PLAIN = new PlainBodyConverter();
    BodyConverter JSON = new JacksonBodyConverter();

}
