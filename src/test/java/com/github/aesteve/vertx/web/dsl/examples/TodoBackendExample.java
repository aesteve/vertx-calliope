package com.github.aesteve.vertx.web.dsl.examples;

import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.io.WebMarshaller;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

        Future<Todo> todoExists(int id) {
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
        WebRouter router = WebRouter.router(Vertx.vertx());
        router.marshaller("application/json", WebMarshaller.JSON);
        router.delete("/api/todos")
                .sendFuture(rc -> todos.clear());
        router.get("/api/todos")
                .sendFuture(rc -> todos.findAll());

        router.post("/api/todos")
                .withBody("todo", Todo.class)
                .sendFuture(rc -> todos.create(rc.get("todo")), 201);

        router.get("/api/todos/:id")
                .intParam("id")
                .sendFuture(rc -> todos.findById(rc.get("id"))); // TODO : if we try to marshall a null value, or an empty optional then return 404 automatically
        /*
        router.put("/api/todos/:id")
                .intParam("id")
                .checkParam("id", todos::todoExists, 404) // TODO : custom status
                .withBody("todo", Todo.class) // TODO : handle request body
                .sendFuture(rc -> todos.update(rc.get("todo"));
        router.delete("/api/todos/:id")
                .intParam("id")
                .checkParam("id", todos::todoExists, 404)
                .sendFuture(rc -> todos.delete(rc.get("id"));
        */
    }

    // Other stuff
    // TODO : String param
    // TODO : non-mandatory params
}
