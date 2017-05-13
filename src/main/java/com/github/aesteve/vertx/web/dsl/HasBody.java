package com.github.aesteve.vertx.web.dsl;

public interface HasBody {

    <T> WebRouteWithPayload<T> withBody(Class<T> bodyClass);

}
