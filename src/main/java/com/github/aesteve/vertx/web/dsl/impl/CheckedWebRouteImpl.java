package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.CheckedWebRoute;
import com.github.aesteve.vertx.web.dsl.WebRoute;
import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiFunction;
import java.util.function.Function;

public class CheckedWebRouteImpl<T> implements CheckedWebRoute {

    private final WebRouteImpl parent;
    private final String ctxName;
    private final Function<RoutingContext, String> getParamValue;
    private final Function<RoutingContext, AsyncResult<T>> checkParamValue;

    CheckedWebRouteImpl(WebRouteImpl parent, BiFunction<HttpServerRequest, String, String> extract, Function<String, AsyncResult<T>> checker, String paramName, String ctxName) {
        this.parent = parent;
        this.ctxName = ctxName;
        getParamValue = rc -> extract.apply(rc.request(), paramName);
        checkParamValue = getParamValue.andThen(checker);
    }

    @Override
    public WebRoute orElse(int status) {
        return parent.handler(rc -> {
            final AsyncResult<T> checked = checkParamValue.apply(rc);
            if (checked.failed()) {
                rc.response().setStatusCode(status).end();
                return;
            }
            rc.put(ctxName, checked.result());
            rc.next();
        });
    }

    @Override
    public WebRoute orElse(HttpError error) {
        return orElse(s -> error);
    }

    @Override
    public WebRoute orElse(Function<String, HttpError> errorSupplier) {
        return orElse(rc -> {
            WebMarshaller m = parent.parent.marshaller(rc);
            HttpError error = getParamValue.andThen(errorSupplier).apply(rc);
            if (m != null) {
                m.toResponseBody(rc, error);
            } else {
                rc.response().setStatusCode(error.status).end();
            }
        });
    }

    @Override
    public WebRoute orElse(Handler<RoutingContext> handler) {
        return parent.handler(rc -> {
            final AsyncResult<T> checked = checkParamValue.apply(rc);
            if (checked.failed()) {
                handler.handle(rc);
                return;
            }
            rc.put(ctxName, checked.result());
            rc.next();
        });
    }

}
