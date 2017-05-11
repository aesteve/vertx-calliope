package com.github.aesteve.vertx.web.dsl.composition;

import com.github.aesteve.vertx.web.dsl.TestBase;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import static com.github.aesteve.vertx.web.dsl.io.BodyConverter.PLAIN;

public class CompositionTest extends TestBase {

    private final static String COMPOSED_ROUTE = "/tests/actions/coposition";

    private final static MockObject PAYLOAD = new MockObject();

    @Override
    protected WebRouter createRouter(Vertx vertx) {
        WebRouter router = WebRouter.router(vertx);
        router.get(COMPOSED_ROUTE)
                .produces("text/plain", PLAIN)
                .action(rc -> PAYLOAD)
                .map(MockObject::getI)
                .send();
        return router;
    }

    @Test
    public void testActionComposition(TestContext ctx) {
        Async async = ctx.async();
        client().getNow(COMPOSED_ROUTE, resp -> {
            ctx.assertEquals(200, resp.statusCode());
            resp.bodyHandler(buffer -> {
                ctx.assertEquals(String.valueOf(PAYLOAD.getI()), buffer.toString());
                async.complete();
            });
        });
    }

}
