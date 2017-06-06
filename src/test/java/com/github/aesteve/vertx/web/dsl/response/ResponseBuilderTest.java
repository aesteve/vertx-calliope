package com.github.aesteve.vertx.web.dsl.response;

import com.github.aesteve.vertx.web.dsl.ResponseBuilder;
import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.github.aesteve.vertx.web.dsl.ResponseBuilder.*;
import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.JSON;
import static io.netty.handler.codec.http.HttpHeaderNames.VARY;
import static io.vertx.core.http.HttpHeaders.*;


public class ResponseBuilderTest extends TestBase {

    private static final String MOCK_OBJECT_URL_200 = "/builder/200/mockobject";
    private static final String MOCK_OBJECT_URL_201 = "/builder/201/mockobject";
    private static final String MOCK_OBJECT_URL_202 = "/builder/202/mockobject";
    private static final String MOCK_OBJECT_URL_203 = "/builder/203/mockobject";
    private static final String MOCK_OBJECT_URL_204 = "/builder/204";
    private static final String MOCK_OBJECT_URL_205 = "/builder/205";
    private static final String MOCK_OBJECT_URL_206 = "/builder/206/mockobject";

    private static final String MOCK_OBJECT_URL_300 = "/builder/300";
    private static final String MOCK_OBJECT_URL_301 = "/builder/301";
    private static final String MOCK_OBJECT_URL_302 = "/builder/302";
    private static final String MOCK_OBJECT_URL_303 = "/builder/303";
    private static final String MOCK_OBJECT_URL_304 = "/builder/304";
    private static final String MOCK_OBJECT_URL_305 = "/builder/305";
    private static final String MOCK_OBJECT_URL_307 = "/temporary/redirect";



    private final static MockObject MOCK = new MockObject();
    private final static String MOCK_CREATED_URL = "/mock/object/created";
    private final static Date NOW = new Date();
    private final static SimpleDateFormat headerDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");


    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(MOCK_OBJECT_URL_200)
                .marshaller("application/json", JSON)
                .lift(rc -> MOCK)
                .fold(ResponseBuilder::ok);
        router.get(MOCK_OBJECT_URL_201)
                .marshaller("application/json", JSON)
                .lift(rc -> MOCK)
                .fold(m -> created(MOCK_CREATED_URL, m));
        router.get(MOCK_OBJECT_URL_202)
                .marshaller("application/json", JSON)
                .lift(rc -> MOCK)
                .fold(ResponseBuilder::accepted);
        router.get(MOCK_OBJECT_URL_203)
                .marshaller("application/json", JSON)
                .lift(rc -> MOCK)
                .fold(ResponseBuilder::nonAuthoritativeInformation);
        router.get(MOCK_OBJECT_URL_204)
                .marshaller("application/json", JSON)
                .lift(rc -> MOCK)
                .send(NO_CONTENT);
        router.get(MOCK_OBJECT_URL_205)
                .send(RESET_CONTENT);
        router.get(MOCK_OBJECT_URL_206)
                .marshaller("application/json", JSON)
                .lift(rc -> MOCK)
                .fold(m ->
                        PARTIAL_CONTENT
                        .cacheControl("cache-control")
                        .contentRange("content-range")
                        .date(NOW)
                        .eTag("e-tag")
                        .expires("expires")
                        .vary("vary")
                        .build(m)
                );

        router.get(MOCK_OBJECT_URL_300)
                .send(multipleChoices("/multiple/choices"));
        router.get(MOCK_OBJECT_URL_301)
                .send(movedPermanently("/moved/permanently"));
        router.get(MOCK_OBJECT_URL_302)
                .send(found("/found"));
        router.get(MOCK_OBJECT_URL_303)
                .send(seeOther("/see/other"));
        router.get(MOCK_OBJECT_URL_304)
                .send(notModified(NOW));
        router.get(MOCK_OBJECT_URL_305)
                .send(useProxy("proxy-url"));
        router.get(MOCK_OBJECT_URL_307)
                .send(temporaryRedirect("/temporary/redirect"));

        return router;
    }

    /** 20x */
    @Test
    public void testResponse200(TestContext ctx) {
        testJson(ctx, MOCK_OBJECT_URL_200, MOCK);
    }

    @Test
    public void testResponse201(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_201, "application/json", 201, expectHeader(LOCATION, MOCK_CREATED_URL), MOCK);
    }

    @Test
    public void testResponse202(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_202, "application/json", 202, null, MOCK);
    }

    @Test
    public void testResponse203(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_203, "application/json", 203, null, MOCK);
    }

    @Test
    public void testResponse204(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_204, "application/json", 204, null, null);
    }


    @Test
    public void testResponse205(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_205, "application/json", 205, null, null);
    }

    @Test
    public void testResponse206(TestContext ctx) {
        Map<CharSequence, String> headers = new HashMap<>();
        headers.put(CACHE_CONTROL, "cache-control");
        headers.put(CONTENT_RANGE, "content-range");
        headers.put(DATE, headerDateFormat.format(NOW));
        headers.put(ETAG, "e-tag");
        headers.put(EXPIRES, "expires");
        headers.put(VARY, "vary");
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_206, "application/json", 206, headers, null);
    }

    /** 30x */
    @Test
    public void testResponse300(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_300, "application/json", 300, expectHeader(LOCATION, "/multiple/choices"), null);
    }

    @Test
    public void testResponse301(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_301, "application/json", 301, expectHeader(LOCATION, "/moved/permanently"), null);
    }

    @Test
    public void testResponse302(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_302, "application/json", 302, expectHeader(LOCATION, "/found"), null);
    }

    @Test
    public void testResponse303(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_303, "application/json", 303, expectHeader(LOCATION, "/see/other"), null);
    }

    @Test
    public void testResponse304(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_304, "application/json", 304, expectHeader(DATE, headerDateFormat.format(NOW)), null);
    }

    @Test
    public void testResponse305(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_305, "application/json", 305, expectHeader(LOCATION, "proxy-url"), null);
    }

    @Test
    public void testResponse307(TestContext ctx) {
        testHttpStatusAndHeader(ctx, MOCK_OBJECT_URL_307, "application/json", 307, expectHeader(LOCATION, "/temporary/redirect"), null);
    }
}

