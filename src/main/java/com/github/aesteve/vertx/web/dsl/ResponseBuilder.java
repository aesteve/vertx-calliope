package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.impl.WebRouteWithPayloadImpl.BODY_ID;
import static io.netty.handler.codec.http.HttpHeaderNames.VARY;
import static io.vertx.core.http.HttpHeaders.*;

public final class ResponseBuilder<T> implements Handler<RoutingContext> {

    private final Handler<RoutingContext> handler;
    private final static SimpleDateFormat headerDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    private ResponseBuilder(Handler<RoutingContext> handler) {
        this.handler = handler;
    }

    private ResponseBuilder(int status, T payload) {
        handler = rc -> {
            rc.response().setStatusCode(status);
            rc.put(BODY_ID, payload);
            rc.next();
        };
    }

    private ResponseBuilder(HttpError error) {
        handler = rc -> {
            rc.put(BODY_ID, error);
            rc.next();
        };
    }

    private ResponseBuilder(int status) {
        handler = rc -> rc.response().setStatusCode(status).end();
    }


    private ResponseBuilder(int status, CharSequence headerName, String headerValue) {
        handler = rc -> rc.response()
                .setStatusCode(status)
                .putHeader(headerName, headerValue)
                .end();
    }

    @Override
    public void handle(RoutingContext context) {
        handler.handle(context);
    }

    /** 10x */
    // TODO : help wanted, please


    /** 20x */
    /* 200 */
    public static Handler<RoutingContext> OK = ok();
    public static Handler<RoutingContext> ok() {
        return new ResponseBuilder<>(200);
    }
    public static <T> Handler<RoutingContext> ok(T body) {
        return new ResponseBuilder<>(200, body);
    }
    /* 201 */
    public static Handler<RoutingContext> CREATED = created();
    public static Handler<RoutingContext> created() {
        return new ResponseBuilder<>(201);
    }
    public static Handler<RoutingContext> created(String location) {
        return new ResponseBuilder<>(201, LOCATION, location);
    }
    /* 202 */
    public static Handler<RoutingContext> ACCEPTED = accepted();
    public static Handler<RoutingContext> accepted() {
        return new ResponseBuilder<>(202);
    }
    public static <T> ResponseBuilder<T> accepted(T payload) {
        return new ResponseBuilder<>(202, payload);
    }
    /* 203 */
    public static Handler<RoutingContext> NON_AUTHORITATIVE_INFORMATION = nonAuthoritativeInformation();
    public static Handler<RoutingContext> nonAuthoritativeInformation() {
        return new ResponseBuilder<>(203);
    }
    public static <T> ResponseBuilder<T> nonAuthoritativeInformation(T payload) {
        return new ResponseBuilder<>(203, payload);
    }
    /* 204 */
    public static Handler<RoutingContext> NO_CONTENT = noContent();
    public static Handler<RoutingContext> noContent() {
        return new ResponseBuilder<>(204);
    }
    public static Handler<RoutingContext> noContent(Map<String, String> additionalHeaders) { /* README ? is this a good idea ? */
        return new ResponseBuilder<>(rc -> {
            rc.response().headers().addAll(additionalHeaders);
            rc.response().setStatusCode(204).end();
        });
    }
    /* 205 */
    public static Handler<RoutingContext> RESET_CONTENT = resetContent();
    public static Handler<RoutingContext> resetContent() {
        return new ResponseBuilder<>(205);
    }
    /* 206 */
    public static class CacheHeaderBuilder {
        private final int status;
        private final Map<String, String> headers = new HashMap<>();
        private CacheHeaderBuilder(int status) {
            this.status = status;
        }
        public CacheHeaderBuilder contentRange(String contentRange) {
            headers.put(CONTENT_RANGE.toString(), contentRange);
            return this;
        }
        public CacheHeaderBuilder date(Date date) {
            headers.put(DATE.toString(), headerDateFormat.format(date));
            return this;
        }
        public CacheHeaderBuilder eTag(String eTag) {
            headers.put(ETAG.toString(), eTag);
            return this;
        }
        public CacheHeaderBuilder expires(String expires) {
            headers.put(EXPIRES.toString(), expires);
            return this;
        }
        public CacheHeaderBuilder cacheControl(String cacheControl) {
            headers.put(CACHE_CONTROL.toString(), cacheControl);
            return this;
        }
        public CacheHeaderBuilder vary(String vary) {
            headers.put(VARY.toString(), vary);
            return this;
        }
        public Handler<RoutingContext> build() {
            return new ResponseBuilder<>(rc -> {
                rc.response().headers().addAll(headers);
                rc.response().setStatusCode(status).end();
            });
        }
    }
    public static CacheHeaderBuilder PARTIAL_CONTENT = partialContent();
    public static CacheHeaderBuilder partialContent() {
        return new CacheHeaderBuilder(206);
    }

    /** 30x */
    /* 300 */
    public static Handler<RoutingContext> MULTIPLE_CHOICES = multipleChoices();
    public static Handler<RoutingContext> multipleChoices() {
        return new ResponseBuilder<>(300);
    }
    public static <T> ResponseBuilder<T> multipleChoices(T payload) {
        return new ResponseBuilder<>(300, payload);
    }
    public static Handler<RoutingContext> multipleChoices(String location) {
        return new ResponseBuilder<>(300, LOCATION, location);
    }
    /* 301 */
    public static Handler<RoutingContext> MOVED_PERMANENTLY = movedPermanently();
    public static Handler<RoutingContext> movedPermanently() {
        return new ResponseBuilder<>(301);
    }
    public static Handler<RoutingContext> movedPermanently(String location) {
        return new ResponseBuilder<>(301, LOCATION, location);
    }
    public static Handler<RoutingContext> movedPermanently(Function<RoutingContext, String> locationBuilder) {
        return new ResponseBuilder<>(rc -> {
            rc.response().putHeader(LOCATION, locationBuilder.apply(rc));
            rc.response().setStatusCode(301).end();
        });
    }
    /* 302 */
    public static Handler<RoutingContext> FOUND = found();
    public static Handler<RoutingContext> found() {
        return new ResponseBuilder<>(302);
    }
    public static Handler<RoutingContext> found(String location) {
        return new ResponseBuilder<>(302, LOCATION, location);
    }
    public static Handler<RoutingContext> found(Function<RoutingContext, String> locationBuilder) {
        return new ResponseBuilder<>(rc -> {
            rc.response().putHeader(LOCATION, locationBuilder.apply(rc));
            rc.response().setStatusCode(302).end();
        });
    }
    /* 303 */
    public static Handler<RoutingContext> SEE_OTHER = seeOther();
    public static Handler<RoutingContext> seeOther() {
        return new ResponseBuilder<>(303);
    }
    public static Handler<RoutingContext> seeOther(String location) {
        return new ResponseBuilder<>(303, LOCATION, location);
    }
    public static Handler<RoutingContext> seeOther(Function<RoutingContext, String> locationBuilder) {
        return new ResponseBuilder<>(rc -> {
            rc.response().putHeader(LOCATION, locationBuilder.apply(rc));
            rc.response().setStatusCode(303).end();
        });
    }
    /* 304 */
    public static CacheHeaderBuilder NOT_MODIFIED = notModified();
    public static CacheHeaderBuilder notModified() {
        return new CacheHeaderBuilder(304);
    }
    public static Handler<RoutingContext> notModified(Date date) {
        return new ResponseBuilder<>(304, DATE, headerDateFormat.format(date));
    }
    /* 305 */
    public static Handler<RoutingContext> useProxy(String location) {
        return new ResponseBuilder<>(305, LOCATION, location);
    }
    /* 307 */
    public static Handler<RoutingContext> TEMPORARY_REDIRECT = temporaryRedirect();
    public static Handler<RoutingContext> temporaryRedirect() {
        return new ResponseBuilder<>(307);
    }
    public static Handler<RoutingContext> temporaryRedirect(String location) {
        return new ResponseBuilder<>(307, LOCATION, location);
    }

}
