package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Function;

public interface CheckedWebRoute {

    WebRoute orElse(int status);
    WebRoute orElse(HttpError error);
    WebRoute orElse(Function<String, HttpError> errorSupplier);
    WebRoute orElse(Handler<RoutingContext> handler);

}
