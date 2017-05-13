package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Function;

public interface CheckedWebRoute<T> {

    WebRouteWithPayload<T> orFail(int status);
    WebRouteWithPayload<T> orFail(HttpError error);
    WebRouteWithPayload<T> orFail(Function<String, HttpError> errorSupplier);
    WebRouteWithPayload<T> orFail(Handler<RoutingContext> handler);

    WebRouteWithPayload<T> orElse(T defaultValue);

}
