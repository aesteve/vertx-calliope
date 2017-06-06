package com.github.aesteve.vertx.web.dsl.examples;

import com.github.aesteve.vertx.web.dsl.ResponseBuilder;
import com.github.aesteve.vertx.web.dsl.WebRouter;
import com.github.aesteve.vertx.web.dsl.errors.HttpError;
import com.github.aesteve.vertx.web.dsl.io.BodyConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.github.aesteve.vertx.web.dsl.ResponseBuilder.created;
import static com.github.aesteve.vertx.web.dsl.ResponseBuilder.noContent;
import static com.github.aesteve.vertx.web.dsl.errors.HttpError.badRequest;
import static com.github.aesteve.vertx.web.dsl.errors.HttpError.notFound;
import static com.github.aesteve.vertx.web.dsl.utils.AsyncUtils.asyncBool;

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

        AsyncResult<Todo> todoExists(Integer id) {
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
        HttpError invalidTodoId = badRequest("Todo identifier is not an integer");
        HttpError cantReadTodo = badRequest("Can't read body as Todo, no taskName provided");
        Function<String, HttpError> todoNotFound = id -> notFound("Todo " + id + " not found");
        Function<Todo, AsyncResult<Todo>> bodyIsValid = asyncBool(t -> t.taskName != null);

        WebRouter router = WebRouter.router(Vertx.vertx());
        router.converter("application/json", BodyConverter.JSON);

        router.delete("/api/todos")
                .lift(rc -> todos.clear())
                .fold(rc -> noContent());

        router.get("/api/todos")
                .lift(rc -> todos.findAll())
                .fold();

        router.post("/api/todos")
                .withBody(Todo.class)
                .map(todos::create)
                .fold(t -> created("/api/todos/" + t.result(), t));

        router.get("/api/todos/:id")
                .intParam("id")
                .orFail(invalidTodoId)
                .foldWithContext((id, rc) -> todos.findById(id));

        router.put("/api/todos/:id")
                .intParam("id").orFail(invalidTodoId)
                .check(todos::todoExists).orFail(todoNotFound)
                .withBody(Todo.class)
                .check(bodyIsValid).orFail(cantReadTodo)
                .map((todo, rc) -> todos.update(rc.get("id"), todo))
                .fold();

        router.delete("/api/todos/:id")
                .intParam("id").orFail(invalidTodoId)
                .check(todos::todoExists).orFail(todoNotFound)
                .foldWithContext((t, rc) -> todos.remove(rc.get("id")));

    }

    // Other stuff
    // TODO : String param
    // TODO : non-mandatory params
}
