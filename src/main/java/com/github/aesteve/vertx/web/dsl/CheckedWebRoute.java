package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Function;

public interface CheckedWebRoute {

    WebRoute orFail(int status);
    WebRoute orFail(HttpError error);
    WebRoute orFail(Function<String, HttpError> errorSupplier);
    WebRoute orFail(Handler<RoutingContext> handler);

}
