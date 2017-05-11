package com.github.aesteve.vertx.web.dsl.examples;

import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/* FIXME : remove this class and make proper doc / examples */
public class TodoBackendExample {

    private final static AtomicInteger i = new AtomicInteger();

    private final static class Todo {
        public final String taskName;
        public Todo(String taskName) { this.taskName = taskName; }
    }

    private final static class AsyncTodoService {
        private final Map<Integer, Todo> todos = new HashMap<>();

        Future<Collection<Todo>> findAll() {
            return Future.succeededFuture(todos.values());
        }

        AsyncResult<Todo> todoExists(int id) {
            return get(id) == null ?
                    Future.failedFuture("Not found") :
                    Future.succeededFuture(get(id));
        }

        Future<Todo> findById(int id) {
            return Future.succeededFuture(get(id));
        }

        Future<Integer> create(Todo todo) {
            final int id = i.incrementAndGet();
            todos.put(id, todo);
            return Future.succeededFuture(id);
        }

        Future<Todo> update(int id, Todo todo) {
            todos.put(id, todo);
            return Future.succeededFuture(todo);
        }

        Future<Void> clear() {
            todos.clear();
            return Future.succeededFuture();
        }

        Future<Todo> remove(int id) {
            Todo t = todos.get(id);
            todos.remove(id);
            return Future.succeededFuture(t);
        }

        private Todo get(int id) {
            return todos.get(id);
        }

    }

    public static void main(String... args) {
        AsyncTodoService todos = new AsyncTodoService();
        Function<Integer, AsyncResult<Todo>> todoExists = todos::todoExists;
        Function<String, Integer> idIsAnInt = Integer::parseInt;
        Function<String, AsyncResult<Todo>> validId = idIsAnInt.andThen(todoExists);

        WebRouter router = WebRouter.router(Vertx.vertx());
        router.marshaller("application/json", BodyConverter.JSON);

        router.delete("/api/todos")
                .action(rc -> todos.clear())
                .send(204);

        router.get("/api/todos")
                .send(rc -> todos.findAll());

        router.post("/api/todos")
                .withBody(Todo.class)
                .map(todos::create)
                .send(201);

        router.get("/api/todos/:id")
                .intParam("id")
                .send(rc -> todos.findById(rc.get("id")));

        router.put("/api/todos/:id")
                .intParam("id")
                .checkParam("id", validId, 404, "Todo not found")
                .withBody(Todo.class)
                .map((todo, rc) -> todos.update(rc.get("id"), todo))
                .send();

        router.delete("/api/todos/:id")
                .intParam("id")
                .checkParam("id", validId, 404, "Todo not found")
                .send(rc -> todos.remove(rc.get("id")));
    }

    // Other stuff
    // TODO : String param
    // TODO : non-mandatory params
}
