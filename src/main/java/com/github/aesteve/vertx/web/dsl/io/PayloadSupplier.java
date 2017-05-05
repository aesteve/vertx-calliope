package com.github.aesteve.vertx.web.dsl.io;

import io.vertx.ext.web.RoutingContext;

@FunctionalInterface
public interface PayloadSupplier<T> {

    T getPayload(RoutingContext ctx);

}
