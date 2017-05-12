package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.*;

public interface WebRoute extends ResponseWritable, ErrorHandling<WebRoute> {

    /* Description */
    WebRoute consumes(String mime);
    default WebRoute consumes(String mime, BodyConverter marshaller) {
        consumes(mime);
        return marshaller(mime, marshaller);
   }
    WebRoute produces(String mime);
    default WebRoute produces(String mime, BodyConverter marshaller) {
        produces(mime);
        return marshaller(mime, marshaller);
    }

    /* Marshalling-stuff */
    WebRoute marshaller(String mime, BodyConverter marshaller);

    /* Handler stuff, backwards-compatibility */
    WebRoute handler(Handler<RoutingContext> handler);
    <T> WebRouteWithPayload<T> action(Function<RoutingContext, T> handler);


    /* Body */
    <T> WebRouteWithPayload<T> withBody(Class<T> bodyClass);

    /* Request checking */
    <T> CheckedWebRoute check(String paramName, String ctxName, BiFunction<HttpServerRequest, String, String> getParam, Function<String, AsyncResult<T>> checker);
    default <T> CheckedWebRoute check(String paramName, BiFunction<HttpServerRequest, String, String> getParam, Function<String, AsyncResult<T>> checker) {
        return check(paramName, paramName, getParam, checker);
    }

    default <T> CheckedWebRoute checkHeader(String name, String ctxName, Function<String, AsyncResult<T>> checker) {
        return check(name, ctxName, HttpServerRequest::getHeader, checker);
    }
    default <T> CheckedWebRoute checkHeader(String name, Function<String, AsyncResult<T>> checker) {
        return checkHeader(name, name, checker);
    }
    default <T> CheckedWebRoute checkParam(String name, String ctxName, Function<String, AsyncResult<T>> checker) {
        return check(name, ctxName, HttpServerRequest::getParam, checker);
    }
    default <T> CheckedWebRoute checkParam(String name, Function<String, AsyncResult<T>> checker) {
        return checkParam(name, name, checker);
    }

    default CheckedWebRoute intParam(String name) {
        return checkParam(name, async(Integer::parseInt));
    }
    default CheckedWebRoute boolParam(String name) {
        return checkParam(name, async(Boolean::valueOf));
    }
    default CheckedWebRoute dateParam(String name, String format) {
        Function<String, AsyncResult<Date>> parsing = d -> {
            try {
                return yield(new SimpleDateFormat(format).parse(d));
            } catch (Exception e) {
                return fail(e);
            }
        };
        return checkParam(name, parsing);
    }



    /* Dealing with request */
    // <T> WebRouteWithParams<T> withParams(Function<MultiMap, AsyncResult<T>> extractor);

}
