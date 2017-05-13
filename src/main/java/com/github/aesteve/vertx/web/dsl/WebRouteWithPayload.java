package com.github.aesteve.vertx.web.dsl;

import io.vertx.ext.web.RoutingContext;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface WebRouteWithPayload<T> extends Checkable<T>, HasBody, ResponseWritable {

    <R> WebRouteWithPayload<R> map(BiFunction<T, RoutingContext, R> mapper);
    default <R> WebRouteWithPayload<R> map(Function<T, R> mapper) {
        return map((body, rc) -> mapper.apply(body));
    }

    void fold(BiConsumer<T, RoutingContext> handler);
    void send(int status);
    default void send() {
        send(200);
    }

}
