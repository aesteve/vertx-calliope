package com.github.aesteve.vertx.web.dsl.io;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

@FunctionalInterface
public interface AsyncPayloadSupplier<T> {

    Future<T> getPayload(RoutingContext ctx);

}
