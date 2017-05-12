package com.github.aesteve.vertx.web.dsl.utils;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;

import java.util.function.Function;

public interface AsyncUtils {

    static <T> Future<T> fail(Throwable t) {
        return Future.failedFuture(t);
    }

    static <T> Future<T> yield(T value) {
        return Future.succeededFuture(value);
    }

    static <A, B> Function<A, AsyncResult<B>> async(Function<A, B> func) {
        return a -> {
            try {
                return Future.succeededFuture(func.apply(a));
            } catch (Exception e) {
                return Future.failedFuture(e);
            }
        };
    }
}
