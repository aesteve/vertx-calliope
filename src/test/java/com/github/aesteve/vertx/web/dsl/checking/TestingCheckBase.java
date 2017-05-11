package com.github.aesteve.vertx.web.dsl.checking;

import com.github.aesteve.vertx.web.dsl.TestBase;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;

public abstract class TestingCheckBase extends TestBase {

    final static String HEADER_NAME = "X-Custom-Header";
    final static String CORRECT_HEADER = "expected_value";


    void testOk(String url, TestContext ctx) {
        Async async = ctx.async();
        client().get(url, resp -> {
            // ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(CORRECT_HEADER, buffer.toString());
                async.complete();
            });
        }).putHeader(HEADER_NAME, CORRECT_HEADER).end();
    }


}
