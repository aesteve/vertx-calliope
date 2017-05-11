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
        return check(paramName, ctxName, getParam, checker, 400, "Invalid ");
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
            } catch (Exception e) {
                return fail(e);
            }
        };
        return checkParam(name, parsing);
    }



    /* Dealing with request */
    // <T> WebRouteWithParams<T> withParams(Function<MultiMap, AsyncResult<T>> extractor);

}
