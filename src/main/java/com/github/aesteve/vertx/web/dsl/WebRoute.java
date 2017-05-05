package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.io.AsyncPayloadSupplier;
import com.github.aesteve.vertx.web.dsl.io.PayloadSupplier;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.*;

public interface WebRoute {

    WebRoute consumes(String mime);
    WebRoute produces(String mime);
    WebRoute handler(Handler<RoutingContext> handler);

    <T> void send(PayloadSupplier<T> supplier);
    <T> void sendFuture(AsyncPayloadSupplier<T> supplier);

    default <T> WebRoute check(String paramName, String ctxName, BiFunction<HttpServerRequest, String, String> getParam, Function<String, AsyncResult<T>> checker, int statusIfFailed, String errorMessage) {
        return handler(rc -> {
            final AsyncResult<T> checked = checker.apply(getParam.apply(rc.request(), paramName));
            if (checked.failed()) {
                rc.response().setStatusCode(statusIfFailed).end(errorMessage); // FIXME : doesn't use the right marshaller if set...
                return;
            }
            rc.put(ctxName, checked.result());
            rc.next();
        });
    }
    default <T> WebRoute check(String paramName, String ctxName, BiFunction<HttpServerRequest, String, String> getParam, Function<String, AsyncResult<T>> checker) {
        return check(paramName, paramName, getParam, checker, 400, "Invalid ");
    }
    default <T> WebRoute check(String paramName, BiFunction<HttpServerRequest, String, String> getParam, Function<String, AsyncResult<T>> checker, int statusIfFailed, String statusReason) {
        return check(paramName, paramName, getParam, checker, statusIfFailed, statusReason);
    }


    default <T> WebRoute checkHeader(String name, String ctxName, Function<String, AsyncResult<T>> checker) {
        return check(name, ctxName, HttpServerRequest::getHeader, checker, 400, "Invalid parameter " + name);
    }
    default <T> WebRoute checkParam(String name, Function<String, AsyncResult<T>> checker) {
        return check(name, name, HttpServerRequest::getParam, checker, 400, "Invalid parameter " + name);
    }
    default <T> WebRoute checkParam(String name, Function<String, AsyncResult<T>> checker, int status, String statusReason) {
        return check(name, name, HttpServerRequest::getParam, checker, status, statusReason);
    }

    default WebRoute intParam(String name) {
        return checkParam(name, async(Integer::parseInt));
    }
    default WebRoute boolParam(String name) {
        return checkParam(name, async(Boolean::valueOf));
    }
    default WebRoute dateParam(String name, String format) {
        Function<String, AsyncResult<Date>> parsing = d -> {
            try {
                return yield(new SimpleDateFormat(format).parse(d));
            } catch (ParseException pe) {
                return fail(pe);
            }
        };
        return checkParam(name, parsing);
    }


}
