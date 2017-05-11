package com.github.aesteve.vertx.web.dsl.io;

import io.vertx.ext.web.RoutingContext;

public interface WebUnmarshaller<Upper> {

    <T extends Upper> T fromRequestBody(RoutingContext context, Class<T> clazz);

}
