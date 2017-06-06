package com.github.aesteve.vertx.web.dsl;

public interface ResponseWritable {

    /* Response marshalling */
    void send(ResponseBuilder<Void> builder);

}
