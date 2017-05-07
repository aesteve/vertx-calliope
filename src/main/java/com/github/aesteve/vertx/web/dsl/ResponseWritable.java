package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.io.AsyncPayloadSupplier;
import com.github.aesteve.vertx.web.dsl.io.PayloadSupplier;

public interface ResponseWritable {

    /* Response marshalling */
    <T> void send(PayloadSupplier<T> supplier, int statusCode);
    <T> void sendFuture(AsyncPayloadSupplier<T> supplier, int StatusCode);
    default <T> void send(PayloadSupplier<T> supplier) {
        send(supplier, 200);
    }
    default <T> void sendFuture(AsyncPayloadSupplier<T> supplier) {
        sendFuture(supplier, 200);
    }

}
