package com.github.aesteve.vertx.web.dsl;

import io.vertx.ext.web.RoutingContext;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface WebRouteWithAsyncPayload<T> extends Checkable<T>, ResponseWritable {

    <R> WebRouteWithPayload<R> flatMap(BiFunction<T, RoutingContext, R> mapper);
    default <R> WebRouteWithPayload<R> flatMap(Function<T, R> mapper) {
        return flatMap((body, rc) -> mapper.apply(body));
    }

    void fold(Function<T, ResponseBuilder> handler);
    default void fold() {
        fold(ResponseBuilder::ok);
    }

}
