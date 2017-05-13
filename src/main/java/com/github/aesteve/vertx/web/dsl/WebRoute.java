package com.github.aesteve.vertx.web.dsl;

import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.function.Function;

public interface WebRoute extends ResponseWritable, ErrorHandling<WebRoute>, ExtractAndCheckable, CanHaveBody {

    /* Description */
    WebRoute consumes(String mime);
    default WebRoute consumes(String mime, BodyConverter marshaller) {
        consumes(mime);
        return marshaller(mime, marshaller);
   }
    WebRoute produces(String mime);
    default WebRoute produces(String mime, BodyConverter marshaller) {
        produces(mime);
        return marshaller(mime, marshaller);
    }

    /* Marshalling-stuff */
    WebRoute marshaller(String mime, BodyConverter marshaller);

    /* Handler stuff, backwards-compatibility */
    WebRoute handler(Handler<RoutingContext> handler);

    <T> WebRouteWithPayload<T> lift(Function<RoutingContext, T> handler);
    <T> WebRouteWithAsyncPayload<T> liftAsync(Function<RoutingContext, Future<T>> handler);


    /* Dealing with request */
    // <T> WebRouteWithParams<T> withParams(Function<MultiMap, AsyncResult<T>> extractor);

}
