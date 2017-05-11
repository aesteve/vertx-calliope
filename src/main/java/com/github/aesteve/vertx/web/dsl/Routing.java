package com.github.aesteve.vertx.web.dsl;

import io.vertx.core.http.HttpMethod;

import static io.vertx.core.http.HttpMethod.*;
import static io.vertx.core.http.HttpMethod.CONNECT;
import static io.vertx.core.http.HttpMethod.HEAD;

public interface Routing {

    /* Routing related */
    WebRoute route(String path);
    WebRoute route(String path, HttpMethod... methods);
    default WebRoute get(String path) {
        return route(path, GET);
    }
    default WebRoute post(String path) {
        return route(path, POST);
    }
    default WebRoute put(String path) {
        return route(path, PUT);
    }
    default WebRoute patch(String path) {
        return route(path, PATCH);
    }
    default WebRoute delete(String path) {
        return route(path, DELETE);
    }
    default WebRoute options(String path) {
        return route(path, OPTIONS);
    }
    default WebRoute trace(String path) {
        return route(path, TRACE);
    }
    default WebRoute connect(String path) {
        return route(path, CONNECT);
    }
    default WebRoute head(String path) {
        return route(path, HEAD);
    }

}
