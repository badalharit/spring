# Core Spring MVC Statement API — Development, Flow, Testing & Deployment

This document explains everything implemented in the **classic Spring MVC** WAR project located at `core-spring-mvc/` (no Spring Boot). It covers:
- Git branch creation
- Spring MVC framework usage and configuration
- Request/response flow end-to-end
- Basic Authentication mechanism
- MongoDB + RabbitMQ integration
- Build, test, and deployment steps

---

## 1) Git branch setup
A new branch was created for the implementation:
- Branch: `blackboxai/core-statement-api`

Command used:
```bash
git checkout -b blackboxai/core-statement-api
```

After implementation, changes were committed on that branch.

> Useful tip: you can inspect changes with `git log --oneline --decorate -n 20`.


---

## 2) Project type & structure (classic Spring MVC WAR)
The project is a **Maven WAR** app.

### Project “Web base” URLs
Since this is a WAR deployed to a servlet container, the root URL depends on your container/context-path.

Common base URL patterns:
- **Context root**: `http://localhost:<tomcatPort>/<context-path>/`
- **POST API**: `http://localhost:<tomcatPort>/<context-path>/statement`
- **Dashboard UI** (if exposed/linked via controllers):
  - `http://localhost:<tomcatPort>/<context-path>/dashboard` (logical URL depends on your controller/view mapping)
- **Logout POST** (used by dashboard.jsp JS):
  - `http://localhost:<tomcatPort>/<context-path>/logout`

> Tip for new users: in curl examples, replace `<context-path>` with whatever your WAR is mounted as in Tomcat (often the WAR name without `.war`).

Key directories:
- `src/main/java/` — Spring configuration, controller(s), service, repository, security, etc.
- `src/main/webapp/WEB-INF/web.xml` — DispatcherServlet registration + filter mapping
- `src/main/webapp/WEB-INF/views/` — JSP views (`login.jsp`, `dashboard.jsp`)
- `pom.xml` — dependencies for Spring MVC, JSON, MongoDB, RabbitMQ

---


## 3) Maven dependencies (pom.xml)
`core-spring-mvc/pom.xml` was updated to include classic Spring MVC dependencies and required integrations:

### Added dependencies
- **Spring MVC / Spring Context**
  - `org.springframework:spring-webmvc`
  - `org.springframework:spring-context`
- **Servlet API** (provided)
  - `jakarta.servlet:jakarta.servlet-api` (scope: `provided`)
- **JSON serialization**
  - `com.fasterxml.jackson.core:jackson-databind`
- **MongoDB (sync Java driver)**
  - `org.mongodb:mongodb-driver-sync`
- **RabbitMQ client (AMQP)**
  - `com.rabbitmq:amqp-client`
- **Logging**
  - `slf4j-api`
  - `logback-classic`

> Note: The terminal showed a Maven warning about a duplicate Jackson `jackson-databind` entry in the effective model. Build still succeeds.

---

## 4) Spring MVC bootstrapping and configuration

### 4.1 Java Config: `WebAppConfig`
File:
- `src/main/java/com/example/core/config/WebAppConfig.java`

Purpose:
- Enables component scanning for the `com.example.core` package.

```java
@Configuration
@ComponentScan(basePackages = "com.example.core")
public class WebAppConfig {}
```

### 4.2 Java Config: `MvcConfig`
File:
- `src/main/java/com/example/core/config/MvcConfig.java`

Purpose:
- Enables Spring MVC (`@EnableWebMvc`).
- Registers an `InternalResourceViewResolver` for JSPs.
- Adds a Jackson message converter for JSON request/response.

Key parts:
- `@EnableWebMvc`
- `ViewResolver` maps logical views to `/WEB-INF/views/*.jsp`
- `MappingJackson2HttpMessageConverter` ensures JSON binding works with `@RequestBody`

---

## 5) DispatcherServlet + security wiring via web.xml
File:
- `src/main/webapp/WEB-INF/web.xml`

This file wires the classic Spring MVC runtime:

### 5.1 DispatcherServlet mapping
- DispatcherServlet is registered with a context parameter containing Spring config classes.

### 5.2 BasicAuthFilter mapping
A servlet filter is registered and mapped specifically to:
- `/statement`

This ensures only requests to the POST endpoint require Basic Auth.

---

## 6) Basic Authentication implementation
File:
- `src/main/java/com/example/core/security/BasicAuthFilter.java`

### How it works
1. Reads `Authorization` header
2. Requires `Authorization: Basic <base64>`
3. Decodes the base64 content to `username:password`
4. Compares with:
   - username: `root`
   - password: `root123`
5. On failure:
   - returns HTTP `401`
   - includes `WWW-Authenticate: Basic realm="statement-api"`
   - returns a JSON error payload
6. On success:
   - calls `chain.doFilter(...)`

Important design detail:
- The filter uses simple hard-coded credentials as specified.

---

## 7) Functional API: POST /statement (JSON in, MongoDB + RabbitMQ out)

### 7.1 Request/Response models
Files:
- `src/main/java/com/example/core/model/StatementRequest.java`
- `src/main/java/com/example/core/model/StatementResponse.java`

#### StatementRequest
- Accepts arbitrary JSON payload.
- Implemented using a map to store all keys from the JSON.

#### StatementResponse
Returns:
- `status` ("ok")
- `mongoId` (inserted MongoDB document id as hex string)
- `rabbitPublished` (boolean)

### 7.2 Controller
File:
- `src/main/java/com/example/core/controller/StatementController.java`

Defines:
- `POST /statement`
- Consumes/Produces JSON

Flow:
- Spring automatically deserializes the request body into `StatementRequest` via Jackson
- Controller calls service method

### 7.3 Service layer
File:
- `src/main/java/com/example/core/service/StatementService.java`

Implements the main business flow:
1. Insert payload into MongoDB
2. Serialize payload back into JSON string
3. Publish JSON payload to RabbitMQ
4. Return response object

---

## 8) MongoDB integration
File:
- `src/main/java/com/example/core/repository/MongoStatementRepository.java`

### Connection details used
- Mongo URI: `mongodb://localhost:27017/`

### Database and collection
- DB: `spring_statements`
- Collection: `statements`

### Insert behavior
- Creates a Mongo `Document` containing:
  - all request payload fields
  - plus `createdAt` (ISO-8601 instant string)
- Inserts into the collection.
- Returns inserted document ObjectId hex string.

---

## 9) RabbitMQ integration
File:
- `src/main/java/com/example/core/rabbit/RabbitStatementPublisher.java`

### Connection details used
- Host: `localhost`
- Port: `5672`
- Username: `root`
- Password: `root123`
- Vhost: `/`

### Publish behavior
- Declares a durable queue named `statements`
- Publishes message to the default exchange (`""`) with routing key `statements`
- Message body is the JSON payload string
- Returns `true/false` based on success

> Note: RabbitMQ management UI URL (`http://localhost:15672/`) is only for viewing/management; the Java client uses AMQP, not HTTP.

---

## 10) Web UI (JSP)

### 10.1 Login page
File:
- `src/main/webapp/WEB-INF/views/login.jsp`

A simple JSP login form exists.

During development, the CSS was adjusted so the login box is centered in the browser window using flexbox:
- wrapper uses `min-height: 100vh` + `display:flex` + `align-items:center` + `justify-content:center`.

### 10.2 Dashboard page
File:
- `src/main/webapp/WEB-INF/views/dashboard.jsp`

A dashboard that displays:
- “JSON Payload Dashboard”
- paginated payload items (via JS rendering)
- logout button

During development, title centering was adjusted without moving the logout button.

---

## 11) Complete technical flow (end-to-end)
Below is the runtime path for `POST /statement`.

### Step A — Client sends request
Client sends:
- `POST /statement`
- header `Authorization: Basic ...`
- header `Content-Type: application/json`
- JSON body

### Step B — BasicAuthFilter authenticates
`BasicAuthFilter` intercepts the request:
- If auth header missing/invalid → 401
- If credentials match → request continues

### Step C — DispatcherServlet routes to StatementController
Spring MVC uses DispatcherServlet to route to:
- `StatementController.statement(...)`

### Step D — Jackson deserializes request body
Spring binds JSON to:
- `StatementRequest` (map-backed payload)

### Step E — Service writes MongoDB
`StatementService.createStatement(...)` calls:
- `MongoStatementRepository.insertPayload(...)`

Mongo is updated with:
- payload fields
- createdAt

Service obtains `mongoId`.

### Step F — Service publishes RabbitMQ message
Service calls:
- `RabbitStatementPublisher.publish(json)`

RabbitMQ receives the original payload JSON.

### Step G — Response returned
Controller returns:
- `StatementResponse` JSON:
  - status=ok
  - mongoId
  - rabbitPublished

---

## 12) Testing steps

### 12.1 Unit/integration tests in project
The repo includes a test class:
- `src/test/java/com/example/core/StatementApiIntegrationTest.java`

In the build commands used so far, tests were skipped:
- `mvn package -DskipTests`

### 12.2 Manual runtime testing (recommended)
Use curl with Basic Auth.

Credentials:
- root / root123

Mongo URI:
- mongodb://localhost:27017/

RabbitMQ:
- AMQP localhost:5672 using root/root123

Example curl:
```bash
curl -i -u root:root123 \
  -H "Content-Type: application/json" \
  -d '{"customerId":123,"type":"demo","amount":42.5}' \
  http://localhost:8080/<context-path>/statement
```

Expected response:
```json
{
  "status": "ok",
  "mongoId": "<insertedId>",
  "rabbitPublished": true
}
```

---

## 13) Build and package (deployment artifact)
### Build
From project root:
```bash
cd core-spring-mvc
mvn package -DskipTests
```

### Output WAR
WAR file generated at:
- `core-spring-mvc/target/core-spring-mvc.war`

---

## 14) Deployment steps (Tomcat or any servlet container)

### 14.1 Deploy the WAR
1. Copy:
   - `core-spring-mvc/target/core-spring-mvc.war`
2. Deploy to a servlet container (e.g., Tomcat).
   - After deployment, your WAR gets mounted under a context-path (often the WAR name).

### 14.2 Start local dependencies
- MongoDB running on: `localhost:27017`
- RabbitMQ running on: `localhost:5672`

### 14.3 Verify URLs
After deploying, verify using these URLs (replace `<context-path>`):
- Login UI (if routed/linked by your container):
  - `http://localhost:<tomcatPort>/<context-path>/` or the JSP route you expose
- Dashboard UI (if routed/linked):
  - `http://localhost:<tomcatPort>/<context-path>/dashboard` (logical)
- REST endpoint:
  - `POST http://localhost:<tomcatPort>/<context-path>/statement`
  - Requires Basic Auth (`root:root123`)
- Logout endpoint used by the dashboard JSP:
  - `POST http://localhost:<tomcatPort>/<context-path>/logout`

---

## 15) Notes and known issues

- Maven warning: duplicate declaration of `jackson-databind` version exists.
  - Build still succeeds.
  - Can be cleaned up later by removing duplicate dependency.

---

## 16) Summary of implemented modules
Java classes added:
- `WebAppConfig` — component scanning
- `MvcConfig` — MVC enablement + view resolver + JSON converter
- `BasicAuthFilter` — Basic Auth security for `/statement`
- `StatementRequest` — JSON payload ingestion model
- `StatementResponse` — JSON response model
- `MongoStatementRepository` — MongoDB insert logic
- `RabbitStatementPublisher` — RabbitMQ publish logic
- `StatementService` — orchestration layer
- `StatementController` — REST endpoint

---

End of documentation.

