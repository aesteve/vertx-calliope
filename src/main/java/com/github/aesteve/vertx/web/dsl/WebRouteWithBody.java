package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface WebRouteWithBody<T> extends ResponseWritable {

    <R> void send(BiFunction<T, RoutingContext, R> handler, int status);
    <R> void sendFuture(BiFunction<T, RoutingContext, Future<R>> handler, int status);

    <R> void send(Function<T, R> handler, int status);
    <R> void sendFuture(Function<T, Future<R>> handler, int status);

    default <R> void send(BiFunction<T, RoutingContext, R> handler) {
        send(handler, 200);
    }
    default <R> void sendFuture(BiFunction<T, RoutingContext, Future<R>> handler) {
        sendFuture(handler, 200);
    }

    default <R> void send(Function<T, R> handler) {
        send(handler, 200);
    }
    default <R> void sendFuture(Function<T, Future<R>> handler) {
        sendFuture(handler, 200);
    }

}
