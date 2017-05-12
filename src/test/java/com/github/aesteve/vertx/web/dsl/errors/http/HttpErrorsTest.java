package com.github.aesteve.vertx.web.dsl.errors.http;

import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(Parameterized.class)
public class HttpErrorsTest {

    private final int expectedStatus;
    private final HttpErrorTest test;

    public HttpErrorsTest(int expectedStatus, HttpErrorTest test) {
        this.expectedStatus = expectedStatus;
        this.test = test;
    }

    private static class HttpErrorTest {
        private final Supplier<HttpError> errorSupplier;
        private final Function<String, HttpError> errorMsgSupplier;

        private HttpErrorTest(Supplier<HttpError> errorSupplier, Function<String, HttpError> errorMsgSupplier) {
            this.errorSupplier = errorSupplier;
            this.errorMsgSupplier = errorMsgSupplier;
        }

        private HttpError error() { return errorSupplier.get(); }
        private HttpError errorMsg(String msg) { return errorMsgSupplier.apply(msg); }
    }

    @Parameterized.Parameters(name = "Test http status {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { 400, new HttpErrorTest(HttpError::badRequest, HttpError::badRequest) },
                { 401, new HttpErrorTest(HttpError::unauthorized, HttpError::unauthorized) },
                { 402, new HttpErrorTest(HttpError::paymentRequired, HttpError::paymentRequired) },
                { 403, new HttpErrorTest(HttpError::forbidden, HttpError::forbidden) },
                { 404, new HttpErrorTest(HttpError::notFound, HttpError::notFound) },

                { 406, new HttpErrorTest(HttpError::notAcceptable, HttpError::notAcceptable) },

                { 408, new HttpErrorTest(HttpError::requestTimeout, HttpError::requestTimeout) },
                { 409, new HttpErrorTest(HttpError::conflict, HttpError::conflict) },
                { 410, new HttpErrorTest(HttpError::gone, HttpError::gone) },
                { 411, new HttpErrorTest(HttpError::lengthRequired, HttpError::lengthRequired) },
                { 412, new HttpErrorTest(HttpError::preconditionFailed, HttpError::preconditionFailed) },
                { 413, new HttpErrorTest(HttpError::requestEntityTooLarge, HttpError::requestEntityTooLarge) },
                { 414, new HttpErrorTest(HttpError::requestURITooLong, HttpError::requestURITooLong) },
                { 415, new HttpErrorTest(HttpError::unsupportedMediaType, HttpError::unsupportedMediaType) },
                { 416, new HttpErrorTest(HttpError::requestRangeNotSatisfiable, HttpError::requestRangeNotSatisfiable) },
                { 417, new HttpErrorTest(HttpError::expectationFailed, HttpError::expectationFailed) }
        });
    }
    private final static String MSG = "The error message";

    @Test
    public void testStatus() {
        testError(test.error(), expectedStatus, null);
        if (test.errorMsgSupplier != null) {
            testError(test.errorMsg(MSG), expectedStatus, MSG);
        }
    }

    private void testError(HttpError error, int expectedStatus, String expectedMessage) {
        assertEquals(expectedStatus, error.status);
        if (expectedMessage == null) {
            assertNull(error.message);
            return;
        }
        assertEquals(expectedMessage, error.message);
    }

}
