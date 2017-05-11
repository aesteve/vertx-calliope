package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.io.PayloadSupplier;

public interface ResponseWritable {

    /* Response marshalling */
    <T> void send(PayloadSupplier<T> supplier, int statusCode);
    default <T> void send(PayloadSupplier<T> supplier) {
        send(supplier, 200);
    }

}
