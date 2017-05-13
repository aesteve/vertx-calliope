package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.CheckedWebRoute;
import com.github.aesteve.vertx.web.dsl.WebRouteWithPayload;
import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CheckedWebRouteImpl<T> implements CheckedWebRoute<T> {

    final WebRouteImpl parent;
    final String ctxName;
    private final Function<RoutingContext, String> getParamValue;
    private final Function<RoutingContext, AsyncResult<T>> checkParamValue;
    private T defaultValue;

    CheckedWebRouteImpl(WebRouteImpl parent, String ctxName, Function<RoutingContext, String> getParamValue, Function<RoutingContext, AsyncResult<T>> checker) {
        this.parent = parent;
        this.ctxName = ctxName;
        this.getParamValue = getParamValue;
        this.checkParamValue = checker;

    }

    CheckedWebRouteImpl(WebRouteImpl parent, BiFunction<HttpServerRequest, String, String> extract, Function<String, AsyncResult<T>> checker, String paramName, String ctxName) {
        this.parent = parent;
        this.ctxName = ctxName;
        getParamValue = rc -> extract.apply(rc.request(), paramName);
        checkParamValue = getParamValue.andThen(checker);
    }

    @Override
    public WebRouteWithPayload<T> orFail(int status) {
        parent.handler(rc -> {
            final AsyncResult<T> checked = checkParamValue.apply(rc);
            if (checked.failed()) {
                rc.response().setStatusCode(status).end();
                return;
            }
            rc.put(ctxName, checked.result());
            rc.next();
        });
        return new WebRouteWithPayloadImpl<>(parent, ctxName);
    }

    @Override
    public WebRouteWithPayload<T> orFail(HttpError error) {
        return orFail(s -> error);
    }

    @Override
    public WebRouteWithPayload<T> orFail(Function<String, HttpError> errorSupplier) {
        orFail(rc -> {
            WebMarshaller m = parent.parent.marshaller(rc);
            HttpError error = getParamValue.andThen(errorSupplier).apply(rc);
            if (m != null) {
                m.toResponseBody(rc, error);
            } else {
                rc.response().setStatusCode(error.status).end();
            }
        });
        return new WebRouteWithPayloadImpl<>(parent, ctxName);
    }

    @Override
    public WebRouteWithPayload<T> orFail(Handler<RoutingContext> handler) {
        parent.handler(rc -> {
            final AsyncResult<T> checked = checkParamValue.apply(rc);
            if (checked.failed()) {
                handler.handle(rc);
                return;
            }
            rc.put(ctxName, checked.result());
            rc.next();
        });
        return new WebRouteWithPayloadImpl<>(parent, ctxName);
    }

    @Override
    public WebRouteWithPayload<T> orElse(T defaultValue) {
        parent.handler(rc -> {
            final AsyncResult<T> checked = checkParamValue.apply(rc);
            if (checked.failed()) {
                rc.put(ctxName, defaultValue);
            } else {
                rc.put(ctxName, checked.result());
            }
            rc.next();
        });
        return new WebRouteWithPayloadImpl<>(parent, ctxName);
    }

}
