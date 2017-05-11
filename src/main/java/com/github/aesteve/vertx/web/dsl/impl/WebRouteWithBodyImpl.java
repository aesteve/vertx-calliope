package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.WebRouteWithBody;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.function.BiFunction;
import java.util.function.Function;

public class WebRouteWithBodyImpl<T> extends WebRouteImpl implements WebRouteWithBody<T> {

    private final static String BODY_ID = "$$vertx-request-body";
    protected WebRouteImpl parent;

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
    @SuppressWarnings("unchecked")
    public <R> WebRouteWithBody<R> map(BiFunction<T, RoutingContext, R> mapper) {
        parent.handler(rc -> {
            rc.put(BODY_ID, mapper.apply(body(rc), rc));
            rc.next();
        });
        return (WebRouteWithBody<R>)this;
    }

    @Override
    public void send(int status) {
        parent.send(this::body, status);
    }

    protected T body(RoutingContext rc) {
        return rc.get(BODY_ID);
    }
}
