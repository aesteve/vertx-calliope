package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.CheckedWebRoute;
import com.github.aesteve.vertx.web.dsl.ResponseBuilder;
import com.github.aesteve.vertx.web.dsl.WebRouteWithAsyncPayload;
import com.github.aesteve.vertx.web.dsl.WebRouteWithPayload;
import com.github.aesteve.vertx.web.dsl.io.PayloadSupplier;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WebRouteWithAsyncPayloadImpl<T> implements WebRouteWithAsyncPayload<T> {

    private final static String DEFAULT_PAYLOAD_ID = "$$vertx-async-payload";

    protected WebRouteImpl parent;

    public WebRouteWithAsyncPayloadImpl(WebRouteImpl parent, Function<RoutingContext, Future<T>> handler) {
        this.parent = parent;
        parent.handler(rc -> {
            handler.apply(rc).setHandler(res -> {
                if (res.failed()) {
                    rc.fail(res.cause());
                    return;
                }
                rc.put(DEFAULT_PAYLOAD_ID, res.result());
                rc.next();
            });
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> WebRouteWithPayload<R> flatMap(BiFunction<T, RoutingContext, R> mapper) {
        parent.handler(rc -> {
            rc.put(DEFAULT_PAYLOAD_ID, mapper.apply(payload(rc), rc));
            rc.next();
        });
        return new WebRouteWithPayloadImpl<>(parent, DEFAULT_PAYLOAD_ID);
    }

    @Override
    public void fold(Function<T, ResponseBuilder> handler) {
        parent.handler(rc -> {
            ResponseBuilder rb = handler.apply(payload(rc));
            rb.accept(rc);
            if (rb.shouldHaveBody) {
                parent.marshall(rc, rb.body);
            }
        });
    }

    @Override
    public void send(ResponseBuilder<Void> handler) {
        parent.send(handler);
    }

    protected T payload(RoutingContext rc) {
        return rc.get(DEFAULT_PAYLOAD_ID);
    }

    @Override
    public <V> CheckedWebRoute<V> check(Function<T, AsyncResult<V>> checker) {
        return new CheckedWebRouteImpl<>(parent, DEFAULT_PAYLOAD_ID, rc -> "lifted", rc -> checker.apply(payload(rc)));
    }

}
