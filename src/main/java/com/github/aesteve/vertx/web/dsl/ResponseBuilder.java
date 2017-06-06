package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.impl.WebRouteWithPayloadImpl.BODY_ID;
import static io.netty.handler.codec.http.HttpHeaderNames.VARY;
import static io.vertx.core.http.HttpHeaders.*;

public final class ResponseBuilder<T> {

    private final Handler<RoutingContext> handler;
    private final static SimpleDateFormat headerDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    public final T body;
    public final boolean shouldHaveBody;

    private ResponseBuilder(Handler<RoutingContext> handler) {
        this.handler = handler;
        body = null;
        shouldHaveBody = false;
    }

    private ResponseBuilder(Handler<RoutingContext> h, T payload) {
        this.handler = rc -> {
            h.handle(rc);
            rc.put(BODY_ID, payload);
        };
        body = payload;
        shouldHaveBody = true;
    }

    ResponseBuilder(int status, T payload) {
        body = payload;
        shouldHaveBody = true;
        handler = rc -> {
            rc.response().setStatusCode(status);
            rc.put(BODY_ID, payload);
        };
    }

    private ResponseBuilder(HttpError error) {
        handler = rc -> {
            rc.put(BODY_ID, error);
        };
        body = null;
        shouldHaveBody = false;
    }

    ResponseBuilder(int status) {
        handler = rc -> rc.response().setStatusCode(status).end();
        body = null;
        shouldHaveBody = false;
    }


    private ResponseBuilder(int status, CharSequence headerName, String headerValue) {
        handler = rc -> rc.response()
                .setStatusCode(status)
                .putHeader(headerName, headerValue)
                .end();
        body = null;
        shouldHaveBody = false;
    }

    private ResponseBuilder(int status, CharSequence headerName, String headerValue, T payload) {
        handler = rc -> {
            rc.response()
                    .setStatusCode(status)
                    .putHeader(headerName, headerValue);
        };
        body = payload;
        shouldHaveBody = true;
    }

    public void accept(RoutingContext rc) {
        handler.handle(rc);
    }

    /** 10x */
    // TODO : help wanted, please


    /** 20x */
    /* 200 */
    public static ResponseBuilder<Void> OK = ok();
    public static ResponseBuilder<Void> ok() {
        return new ResponseBuilder<>(200);
    }
    public static <T> ResponseBuilder<T> ok(T body) {
        return new ResponseBuilder<>(200, body);
    }
    /* 201 */
    public static ResponseBuilder<Void> CREATED = created();
    public static ResponseBuilder<Void> created() {
        return new ResponseBuilder<>(201);
    }
    public static ResponseBuilder<Void> created(String location) {
        return new ResponseBuilder<>(201, LOCATION, location);
    }
    public static <T> ResponseBuilder<T> created(String location, T payload) {
        return new ResponseBuilder<>(201, LOCATION, location, payload);
    }
    /* 202 */
    public static ResponseBuilder<Void> ACCEPTED = accepted();
    public static ResponseBuilder<Void> accepted() {
        return new ResponseBuilder<>(202);
    }
    public static <T> ResponseBuilder<T> accepted(T payload) {
        return new ResponseBuilder<>(202, payload);
    }
    /* 203 */
    public static ResponseBuilder<Void> NON_AUTHORITATIVE_INFORMATION = nonAuthoritativeInformation();
    public static ResponseBuilder<Void> nonAuthoritativeInformation() {
        return new ResponseBuilder<>(203);
    }
    public static <T> ResponseBuilder<T> nonAuthoritativeInformation(T payload) {
        return new ResponseBuilder<>(203, payload);
    }
    /* 204 */
    public static ResponseBuilder<Void> NO_CONTENT = noContent();
    public static ResponseBuilder<Void> noContent() {
        return new ResponseBuilder<>(204);
    }
    public static ResponseBuilder<Void> noContent(Map<String, String> additionalHeaders) { /* README ? is this a good idea ? */
        return new ResponseBuilder<>(rc -> {
            rc.response().headers().addAll(additionalHeaders);
            rc.response().setStatusCode(204).end();
        });
    }
    /* 205 */
    public static ResponseBuilder<Void> RESET_CONTENT = resetContent();
    public static ResponseBuilder<Void> resetContent() {
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
        public <T> ResponseBuilder<T> build(T payload) {
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
    public static ResponseBuilder<Void> MULTIPLE_CHOICES = multipleChoices();
    public static ResponseBuilder<Void> multipleChoices() {
        return new ResponseBuilder<>(300);
    }
    public static ResponseBuilder<Void> multipleChoices(String location) {
        return new ResponseBuilder<>(300, LOCATION, location);
    }
    /* 301 */
    public static ResponseBuilder<Void> MOVED_PERMANENTLY = movedPermanently();
    public static ResponseBuilder<Void> movedPermanently() {
        return new ResponseBuilder<>(301);
    }
    public static ResponseBuilder<Void> movedPermanently(String location) {
        return new ResponseBuilder<>(301, LOCATION, location);
    }
    public static ResponseBuilder<Void> movedPermanently(Function<RoutingContext, String> locationBuilder) {
        return new ResponseBuilder<>(rc -> {
            rc.response().putHeader(LOCATION, locationBuilder.apply(rc));
            rc.response().setStatusCode(301).end();
        });
    }
    /* 302 */
    public static ResponseBuilder<Void> FOUND = found();
    public static ResponseBuilder<Void> found() {
        return new ResponseBuilder<>(302);
    }
    public static ResponseBuilder<Void> found(String location) {
        return new ResponseBuilder<>(302, LOCATION, location);
    }
    public static <T> ResponseBuilder<T> found(String location, T payload) {
        return new ResponseBuilder<>(302, LOCATION, location, payload);
    }
    public static ResponseBuilder<Void> found(Function<RoutingContext, String> locationBuilder) {
        return new ResponseBuilder<>(rc -> {
            rc.response().putHeader(LOCATION, locationBuilder.apply(rc));
            rc.response().setStatusCode(302).end();
        });
    }
    /* 303 */
    public static ResponseBuilder<Void> SEE_OTHER = seeOther();
    public static ResponseBuilder<Void> seeOther() {
        return new ResponseBuilder<>(303);
    }
    public static ResponseBuilder<Void> seeOther(String location) {
        return new ResponseBuilder<>(303, LOCATION, location);
    }
    public static ResponseBuilder<Void> seeOther(Function<RoutingContext, String> locationBuilder) {
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
    public static ResponseBuilder<Void> notModified(Date date) {
        return new ResponseBuilder<>(304, DATE, headerDateFormat.format(date));
    }
    /* 305 */
    public static ResponseBuilder<Void> useProxy(String location) {
        return new ResponseBuilder<>(305, LOCATION, location);
    }
    /* 307 */
    public static ResponseBuilder<Void> TEMPORARY_REDIRECT = temporaryRedirect();
    public static ResponseBuilder<Void> temporaryRedirect() {
        return new ResponseBuilder<>(307);
    }
    public static ResponseBuilder<Void> temporaryRedirect(String location) {
        return new ResponseBuilder<>(307, LOCATION, location);
    }

}
