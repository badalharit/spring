# core-spring-mvc (classic, no Spring Boot)

WAR-style Spring MVC app that exposes a protected POST endpoint:

- **POST** `/statement`
- **Basic Auth** required (`root` / `root123`)
- **Consumes/Produces** JSON
- Inserts the received JSON into **MongoDB** (`mongodb://localhost:27017/`)
- Publishes the received JSON to **RabbitMQ** (AMQP at `amqp://localhost:5672/`, creds `root` / `root123`, vhost `/`)

## Endpoint

### Request
- URL: `/statement`
- Method: `POST`
- Headers:
  - `Authorization: Basic base64(root:root123)`
  - `Content-Type: application/json`

Example body:
```json
{
  "customerId": 123,
  "type": "demo",
  "amount": 42.5
}
```

### Response
```json
{
  "status": "ok",
  "mongoId": "<insertedId>",
  "rabbitPublished": true
}
```

## MongoDB
- Connection: `mongodb://localhost:27017/`
- DB: `spring_statements`
- Collection: `statements`
- Stored fields:
  - all JSON fields from request payload
  - `createdAt` (ISO-8601 string)

## RabbitMQ
- Host: `localhost`
- Port: `5672`
- Queue: `statements` (declared durable)
- vhost: `/`

> Note: RabbitMQ “management UI” URL (`http://localhost:15672/`) is not used by the Java client.

## Build

```bash
cd core-spring-mvc
mvn package -DskipTests
```

WAR output:
- `target/core-spring-mvc.war`

## Deploy
Deploy the WAR to any Java EE servlet container (e.g., Tomcat).

## Test with curl (example)

Base64 for `root:root123`:
- `root:root123` → `cm9vdDpyb290MTIz`

```bash
curl -i -u root:root123 \
  -H "Content-Type: application/json" \
  -d '{"customerId":123,"type":"demo"}' \
  http://localhost:8080/core-spring-mvc/statement
```

