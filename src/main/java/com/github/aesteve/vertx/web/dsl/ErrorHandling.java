package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public interface ErrorHandling<T> {

    T withErrorDetails(boolean details);
    T onError(Handler<RoutingContext> errorHandler);
    Handler<RoutingContext> errorHandler();

}
