# Core Spring MVC directory structure (what you should learn)

A typical **classic Spring MVC** app is structured like a WAR project:

## 1) Source code layout

### `src/main/java`
- Package(s) for the application code
- Usually split into:
  - `controller/` (web layer)
  - `service/` (business logic)
  - `repository/` (data access)
  - `config/` (Spring @Configuration classes)
  - `model/` (domain objects)

Example:
```
src/main/java/com/example/app/
  controller/
  service/
  repository/
  model/
  config/
```

### `src/test/java`
- Unit/integration tests

## 2) Web layer layout (`src/main/webapp`)

### `src/main/webapp/WEB-INF`
- Server-protected resources (not directly accessible)

Common parts:
- `web.xml`
- `views/` (JSP/Thymeleaf/etc depending on setup)
- static assets can be under `resources/` or `static/` (not protected)

Example:
```
src/main/webapp/
  resources/
    css/
    js/
  WEB-INF/
    web.xml
    views/
      hello.jsp
```

## 3) Where Spring config goes

Classic Spring MVC has two common options:
1. XML config (legacy style)
2. Java config (`@Configuration`)

This project uses Java config for convenience.

## 4) DispatcherServlet role

In Spring MVC, `DispatcherServlet` is the front controller.
In classic projects it is registered in `WEB-INF/web.xml`:

- URL mapping: e.g. `/` or `/app/*`
- `contextConfigLocation`: where Spring MVC configuration lives

## 5) View rendering

A `ViewResolver` maps a logical view name returned by controllers:
- `"hello"` -> `/WEB-INF/views/hello.jsp`

This is configured in Spring MVC config.

