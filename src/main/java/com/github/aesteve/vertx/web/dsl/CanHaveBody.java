package com.github.aesteve.vertx.web.dsl;

public interface CanHaveBody {

    <T> WebRouteWithPayload<T> withBody(Class<T> bodyClass);

}
