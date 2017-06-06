package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface WebRouteWithPayload<T> extends Checkable<T>, CanHaveBody, ResponseWritable {

    <R> WebRouteWithPayload<R> map(BiFunction<T, RoutingContext, R> mapper);
    default <R> WebRouteWithPayload<R> map(Function<T, R> mapper) {
        return map((body, rc) -> mapper.apply(body));
    }

    void foldWithContext(BiConsumer<T, RoutingContext> handler);
    void foldWithResponse(BiConsumer<T, HttpServerResponse> handler);
    void fold(Function<T, ResponseBuilder> handler);
    default void fold() {
        fold(ResponseBuilder::ok);
    }
    default void fold(int status) {
        fold(t -> new ResponseBuilder<>(status, t));
    }

}
