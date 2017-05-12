package com.github.aesteve.vertx.web.dsl.errors.http;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.errors.HttpError.*;
import static io.vertx.core.http.HttpHeaders.*;
import static org.junit.Assert.*;

/* Corner cases */
public class CornerCasesHttpErrorsTest {

    @Test
    public void test405() {
        HttpError error = methodNotAllowed("POST");
        assertEquals(405, error.status);
        assertEquals(1, error.additionalHeaders.size());
        assertEquals("POST", error.additionalHeaders.get(ALLOW.toString()));
    }

    @Test
    public void test407() {
        String proxyUrl = "http://some.proxy.host";
        HttpError error = proxyAuthenticationRequired(proxyUrl);
        assertEquals(407, error.status);
        assertEquals(1, error.additionalHeaders.size());
        assertEquals(proxyUrl, error.additionalHeaders.get(LOCATION.toString()));
    }

    @Test
    public void test413() {
        String retryAfter = "10";
        HttpError error = requestEntityTooLarge(null, retryAfter);
        assertEquals(413, error.status);
        assertEquals(1, error.additionalHeaders.size());
        assertNull(error.message);
        assertEquals(retryAfter, error.additionalHeaders.get(RETRY_AFTER.toString()));
    }

    @Test
    public void test415() {
        String mime = "my/media";
        String msg = "Don't know this media type";
        HttpError error = unsupportedMediaType(msg, mime);
        assertEquals(415, error.status);
        assertEquals(1, error.additionalHeaders.size());
        assertNotNull(error.message);
        assertEquals(msg, error.message);
        assertEquals(mime, error.additionalHeaders.get(ACCEPT.toString()));
    }

    @Test
    public void test416() {
        String range = "The Range";
        HttpError error = requestRangeNotSatisfiable(null, range);
        assertEquals(416, error.status);
        assertEquals(1, error.additionalHeaders.size());
        assertNull(error.message);
        assertEquals(range, error.additionalHeaders.get(CONTENT_RANGE.toString()));
    }

}
