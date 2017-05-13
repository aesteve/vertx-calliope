package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.AsyncResult;
import io.vertx.core.http.HttpServerRequest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.async;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.fail;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.yield;

public interface ExtractAndCheckable {

    /* Request checking */
    <T> CheckedWebRoute<T> check(String paramName, String ctxName, BiFunction<HttpServerRequest, String, String> getParam, Function<String, AsyncResult<T>> checker);
    default <T> CheckedWebRoute<T> check(String paramName, BiFunction<HttpServerRequest, String, String> getParam, Function<String, AsyncResult<T>> checker) {
        return check(paramName, paramName, getParam, checker);
    }

    default <T> CheckedWebRoute<T> checkHeader(String name, String ctxName, Function<String, AsyncResult<T>> checker) {
        return check(name, ctxName, HttpServerRequest::getHeader, checker);
    }
    default <T> CheckedWebRoute<T> checkHeader(String name, Function<String, AsyncResult<T>> checker) {
        return checkHeader(name, name, checker);
    }
    default <T> CheckedWebRoute<T> checkParam(String name, String ctxName, Function<String, AsyncResult<T>> checker) {
        return check(name, ctxName, HttpServerRequest::getParam, checker);
    }
    default <T> CheckedWebRoute<T> checkParam(String name, Function<String, AsyncResult<T>> checker) {
        return checkParam(name, name, checker);
    }

    default CheckedWebRoute<Integer> intParam(String name) {
        return checkParam(name, async(Integer::parseInt));
    }
    default CheckedWebRoute<Boolean> boolParam(String name) {
        return checkParam(name, async(Boolean::valueOf));
    }
    default CheckedWebRoute<Date> dateParam(String name, String format) {
        Function<String, AsyncResult<Date>> parsing = d -> {
            try {
                return yield(new SimpleDateFormat(format).parse(d));
            } catch (Exception e) {
                return fail(e);
            }
        };
        return checkParam(name, parsing);
    }



}
