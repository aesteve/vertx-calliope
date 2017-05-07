package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.WebRouteWithBody;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.function.BiFunction;
import java.util.function.Function;

public class WebRouteWithBodyImpl<T> extends WebRouteImpl implements WebRouteWithBody<T> {

    private final static String BODY_ID = "$$vertx-request-body";
    private WebRouteImpl parent;

    public WebRouteWithBodyImpl(WebRouteImpl parent, Class<T> bodyClass) {
        super(parent);
        this.parent = parent;
        parent.handler(BodyHandler.create());
        parent.handler(rc -> {
            parent.withMarshaller(rc, m -> {
                rc.put(BODY_ID, m.fromRequestBody(rc, bodyClass));
                rc.next();
            });
        });
    }

    @Override
    public <R> void send(BiFunction<T, RoutingContext, R> handler, int status) {
        parent.send(rc -> handler.apply(body(rc), rc), status);
    }

    @Override
    public <R> void sendFuture(BiFunction<T, RoutingContext, Future<R>> handler, int status) {
        parent.sendFuture(rc -> handler.apply(body(rc), rc), status);
    }

    @Override
    public <R> void send(Function<T, R> handler, int status) {
        parent.send(rc -> handler.apply(body(rc)), status);
    }

    @Override
    public <R> void sendFuture(Function<T, Future<R>> handler, int status) {
        parent.sendFuture(rc -> handler.apply(body(rc)), status);
    }

    private T body(RoutingContext rc) {
        return rc.get(BODY_ID);
    }
}
