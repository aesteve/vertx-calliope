[![Travis](https://img.shields.io/travis/aesteve/vertx-calliope.svg)](https://travis-ci.org/aesteve/vertx-calliope)
[![Codecov](https://img.shields.io/codecov/c/github/aesteve/vertx-calliope.svg)](https://codecov.io/gh/aesteve/vertx-calliope)

## Vert.x Calliope

Just an experiment, to test different approaches to bring more expressiveness to vertx-web route declaration.

Mostly :

- declaring expected request param/header types (and automatically checking them)
- marshalling / unmarshalling body
- declaring expected body class / body return
- declaring checks and associated status codes
- using `map` in a functional way directly on routes

## TODO : 

- add the ability to register marshaller / unmarshaller & not only a full converter
- params / headers marshalling in an single object `intParam`, `dateParam` becoming a simple corner case
- `map` with body / params / headers at the same time i.e. 
```
router.get("/...")
  .withBody(MyBody.class)
  .withParams(MyParams.class)
  .map((body, params) -> { ... })
  .send(200);
```
- route nesting, i.e.
```
router.route("/api")
  .consumes(...)
  .get("/tests")
  ...
```
