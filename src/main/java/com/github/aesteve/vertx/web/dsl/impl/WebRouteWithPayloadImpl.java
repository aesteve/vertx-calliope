package com.github.aesteve.vertx.web.dsl.impl;

import com.github.aesteve.vertx.web.dsl.CheckedWebRoute;
import com.github.aesteve.vertx.web.dsl.WebRouteWithPayload;
import io.vertx.core.AsyncResult;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WebRouteWithPayloadImpl<T> extends WebRouteImpl implements WebRouteWithPayload<T> {

    public final static String BODY_ID = "$$vertx-request-payload";
    protected WebRouteImpl parent;
    private String ctxName;
    private Function<RoutingContext, String> rawPayloadSupplier;

    public WebRouteWithPayloadImpl(WebRouteImpl parent, Class<T> bodyClass) {
        super(parent);
        this.parent = parent;
        this.rawPayloadSupplier = RoutingContext::getBodyAsString;
        parent.handler(BodyHandler.create());
        parent.handler(rc -> {
            parent.withUnmarshaller(rc, m -> {
                rc.put(BODY_ID, m.fromRequestBody(rc, bodyClass));
                if (!rc.failed()) {
                    rc.next();
                }
            });
        });
    }

    public WebRouteWithPayloadImpl(WebRouteImpl parent, Function<RoutingContext, T> handler, Function<RoutingContext, String> rawPayloadSupplier) {
        super(parent);
        this.parent = parent;
        this.rawPayloadSupplier = rawPayloadSupplier;
        parent.handler(rc -> {
            rc.put(BODY_ID, handler.apply(rc));
            if (!rc.failed()) {
                rc.next();
            }
        });
    }


    public WebRouteWithPayloadImpl(WebRouteImpl parent, String ctxName) {
        super(parent);
        this.parent = parent;
        this.ctxName = ctxName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> WebRouteWithPayload<R> map(BiFunction<T, RoutingContext, R> mapper) {
        parent.handler(rc -> {
            rc.put(BODY_ID, mapper.apply(payload(rc), rc));
            rc.next();
        });
        return (WebRouteWithPayload<R>)this;
    }

    @Override
    public void fold(BiConsumer<T, RoutingContext> handler) {
        parent.handler(rc -> {
            handler.accept(payload(rc), rc);
        });
    }

    @Override
    public void send(int status) {
        parent.send(this::payload, status);
    }

    protected T payload(RoutingContext rc) {
        return rc.get(ctxName == null ? BODY_ID : ctxName);
    }

    @Override
    public <V> CheckedWebRoute<V> check(Function<T, AsyncResult<V>> checker) {
        return new CheckedWebRouteImpl<>(parent, ctxName, rawPayloadSupplier, rc -> checker.apply(payload(rc)));
    }
}
