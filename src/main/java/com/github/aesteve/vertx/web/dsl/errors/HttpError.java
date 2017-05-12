package com.github.aesteve.vertx.web.dsl.errors;

import io.vertx.core.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

import static io.vertx.core.http.HttpHeaders.*;

public class HttpError {

    public final int status;
    public final String message;
    public final Map<String, String> additionalHeaders = new HashMap<>();

    public HttpError(int status) {
        this.status = status;
        message = null;
    }

    public HttpError(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpError header(CharSequence header, String value) {
        additionalHeaders.put(header.toString(), value);
        return this;
    }


    /** 4xx */
    /* 400 */
    public static HttpError BAD_REQUEST = badRequest();
    public static HttpError badRequest() {
        return new HttpError(400);
    }
    public static HttpError badRequest(String message) {
        return new HttpError(400, message);
    }

    /* 401 */
    public static HttpError UNAUTHORIZED = unauthorized();
    public static HttpError unauthorized() {
        return new HttpError(401);
    }
    public static HttpError unauthorized(String message) {
        return new HttpError(401, message);
    }
    /* 402 */
    public static HttpError PAYMENT_REQUIRED = paymentRequired();
    public static HttpError paymentRequired() {
        return new HttpError(402);
    }
    public static HttpError paymentRequired(String message) {
        return new HttpError(402, message);
    }
    /* 403 */
    public static HttpError FORBIDDEN = forbidden();
    public static HttpError forbidden() {
        return new HttpError(403);
    }
    public static HttpError forbidden(String message) {
        return new HttpError(403, message);
    }
    /* 404 */
    public static HttpError NOT_FOUND = notFound();
    public static HttpError notFound() {
        return new HttpError(404);
    }
    public static HttpError notFound(String message) {
        return new HttpError(404, message);
    }
    /* 405 */
    public static HttpError methodNotAllowed(String allow) {
        return new HttpError(405, "Method not allowed").header(ALLOW, allow);
    }
    /* 406 */
    public static HttpError NOT_ACCEPTABLE = notAcceptable();
    public static HttpError notAcceptable() {
        return new HttpError(406);
    }
    public static HttpError notAcceptable(String message) {
        return new HttpError(406, message);
    }
    /* 407 */
    public static HttpError proxyAuthenticationRequired(String proxyLocation) {
        return new HttpError(407).header(LOCATION, proxyLocation);
    }
    /* 408 */
    public static HttpError REQUEST_TIMEOUT = requestTimeout();
    public static HttpError requestTimeout() {
        return new HttpError(408);
    }
    public static HttpError requestTimeout(String message) {
        return new HttpError(408, message);
    }
    /* 409 */
    public static HttpError CONFLICT = conflict();
    public static HttpError conflict() {
        return new HttpError(409);
    }
    public static HttpError conflict(String message) {
        return new HttpError(409, message);
    }
    /* 410 */
    public static HttpError GONE = gone();
    public static HttpError gone() {
        return new HttpError(410);
    }
    public static HttpError gone(String message) {
        return new HttpError(410, message);
    }
    /* 411 */
    public static HttpError LENGTH_REQUIRED = lengthRequired();
    public static HttpError lengthRequired() {
        return new HttpError(411);
    }
    public static HttpError lengthRequired(String message) {
        return new HttpError(411, message);
    }
    /* 412 */
    public static HttpError PRECONDITION_FAILED = preconditionFailed();
    public static HttpError preconditionFailed() {
        return new HttpError(412);
    }
    public static HttpError preconditionFailed(String message) {
        return new HttpError(412, message);
    }
    /* 413 */
    public static HttpError REQUEST_ENTITY_TOO_LARGE = requestEntityTooLarge();
    public static HttpError requestEntityTooLarge() {
        return new HttpError(413);
    }
    public static HttpError requestEntityTooLarge(String message) {
        return new HttpError(413, message);
    }
    public static HttpError requestEntityTooLarge(String message, String delay) {
        return new HttpError(413, message).header(HttpHeaders.RETRY_AFTER, delay);
    }
    /* 414 */
    public static HttpError REQUEST_URI_TOO_LONG = requestURITooLong();
    public static HttpError requestURITooLong() {
        return new HttpError(414);
    }
    public static HttpError requestURITooLong(String message) {
        return new HttpError(414, message);
    }
    /* 415 */
    public static HttpError UNSUPPORTED_MEDIA_TYPE = unsupportedMediaType();
    public static HttpError unsupportedMediaType() {
        return new HttpError(415);
    }
    public static HttpError unsupportedMediaType(String message) {
        return new HttpError(415, message);
    }
    public static HttpError unsupportedMediaType(String message, String accept) {
        return new HttpError(415, message).header(ACCEPT, accept);
    }
    /* 416 */
    public static HttpError REQUEST_RANGE_NOT_SATISFIABLE = requestRangeNotSatisfiable();
    public static HttpError requestRangeNotSatisfiable() {
        return new HttpError(416);
    }
    public static HttpError requestRangeNotSatisfiable(String message, String range) {
        return new HttpError(416, message).header(CONTENT_RANGE, range);
    }
    public static HttpError requestRangeNotSatisfiable(String message) {
        return new HttpError(416, message);
    }
    /* 417 */
    public static HttpError EXPECTATION_FAILED = expectationFailed();
    public static HttpError expectationFailed() {
        return new HttpError(417);
    }
    public static HttpError expectationFailed(String message) {
        return new HttpError(417, message);
    }

    /** 50x */
    /* 500 */
    public static HttpError INTERNAL_SERVER_ERROR = internalServerError();
    public static HttpError internalServerError() {
        return new HttpError(500);
    }
    public static HttpError internalServerError(String message) {
        return new HttpError(500, message);
    }
    /* 501 */
    public static HttpError NOT_IMPLEMENTED = notImplemented();
    public static HttpError notImplemented() {
        return new HttpError(501);
    }
    public static HttpError notImplemented(String message) {
        return new HttpError(501, message);
    }
    /* 502 */
    public static HttpError BAD_GATEWAY = badGateway();
    public static HttpError badGateway() {
        return new HttpError(502);
    }
    public static HttpError badGateway(String message) {
        return new HttpError(502, message);
    }
    /* 503 */
    public static HttpError SERVICE_UNAVAILABLE = serviceUnavailable();
    public static HttpError serviceUnavailable() {
        return new HttpError(503);
    }
    public static HttpError serviceUnavailable(String message) {
        return new HttpError(503, message);
    }
    public static HttpError serviceUnavailable(String message, String retryAfter) {
        return new HttpError(503, message).header(RETRY_AFTER, retryAfter);
    }
    /* 504 */
    public static HttpError GATEWAY_TIMEOUT = gatewayTimeout();
    public static HttpError gatewayTimeout() {
        return new HttpError(504);
    }
    public static HttpError gatewayTimeout(String message) {
        return new HttpError(504, message);
    }
    /* 505 */
    public static HttpError HTTP_VERSION_NOT_SUPPORTED = httpVersionNotSupported();
    public static HttpError httpVersionNotSupported() {
        return new HttpError(505);
    }
    public static HttpError httpVersionNotSupported(String message) {
        return new HttpError(505, message);
    }

}
