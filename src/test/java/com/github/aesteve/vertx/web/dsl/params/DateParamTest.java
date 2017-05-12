package com.github.aesteve.vertx.web.dsl.params;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParamTest extends TestBase {

    private final static String DATE_PARAM_URL = "/tests/params/date";
    private final static String EXPECTED_FMT = "dd/MM/yyyy";

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(DATE_PARAM_URL)
                .dateParam("date", EXPECTED_FMT).orElse(400)
                .handler(rc -> {
                    Date d = rc.get("date");
                    rc.response().end(String.valueOf(d.getTime()));
                });
        return router;
    }

    @Test
    public void noDateParamShouldEndUpIn400(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(DATE_PARAM_URL, resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void wrongFormatShouldEndUpIn400(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(DATE_PARAM_URL + "?date=01-01-2017", resp -> {
            ctx.assertEquals(400, resp.statusCode());
            async.complete();
        });
    }

    @Test
    public void rightFormatShouldBeOk(TestContext ctx) throws Exception {
        Async async = ctx.async();
        SimpleDateFormat fmt = new SimpleDateFormat(EXPECTED_FMT);
        String sent = fmt.format(NOW);
        Date expected = fmt.parse(sent);
        client().getNow(DATE_PARAM_URL + "?date=" + sent, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(String.valueOf(expected.getTime()), buffer.toString());
                async.complete();
            });
        });
    }

}
