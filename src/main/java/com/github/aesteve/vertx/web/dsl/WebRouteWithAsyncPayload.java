package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface WebRouteWithAsyncPayload<T> extends Checkable<T> {

    <R> WebRouteWithPayload<R> flatMap(BiFunction<T, RoutingContext, R> mapper);
    default <R> WebRouteWithPayload<R> flatMap(Function<T, R> mapper) {
        return flatMap((body, rc) -> mapper.apply(body));
    }

    void fold(BiConsumer<T, RoutingContext> handler);
    void send(int status);
    default void send() {
        send(200);
    }

}
