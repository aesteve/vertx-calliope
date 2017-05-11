package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.WebRouteWithPayload;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.function.BiFunction;
import java.util.function.Function;

public class WebRouteWithPayloadImpl<T> extends WebRouteImpl implements WebRouteWithPayload<T> {

    private final static String BODY_ID = "$$vertx-request-body";
    protected WebRouteImpl parent;

    public WebRouteWithPayloadImpl(WebRouteImpl parent, Class<T> bodyClass) {
        super(parent);
        this.parent = parent;
        parent.handler(BodyHandler.create());
        parent.handler(rc -> {
            parent.withMarshaller(rc, m -> {
                rc.put(BODY_ID, m.fromRequestBody(rc, bodyClass));
                if (!rc.failed()) {
                    rc.next();
                }
            });
        });
    }

    public WebRouteWithPayloadImpl(WebRouteImpl parent, Function<RoutingContext, T> handler) {
        super(parent);
        this.parent = parent;
        parent.handler(rc -> {
            rc.put(BODY_ID, handler.apply(rc));
            if (!rc.failed()) {
                rc.next();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> WebRouteWithPayload<R> map(BiFunction<T, RoutingContext, R> mapper) {
        parent.handler(rc -> {
            rc.put(BODY_ID, mapper.apply(body(rc), rc));
            rc.next();
        });
        return (WebRouteWithPayload<R>)this;
    }

    @Override
    public void send(int status) {
        parent.send(this::body, status);
    }

    private T body(RoutingContext rc) {
        return rc.get(BODY_ID);
    }
}
