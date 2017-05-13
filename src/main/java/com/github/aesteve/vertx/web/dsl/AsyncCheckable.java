package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.AsyncResult;

import java.util.function.Function;

public interface AsyncCheckable<T> {

    /* Allows to check and map at the same time */
    <V> CheckedWebRoute<V> check(Function<T, AsyncResult<V>> checker);

}
