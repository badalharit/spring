# core-spring-mvc (classic Core Spring MVC, no Spring Boot)

This project is a **classic** Spring MVC (WAR) skeleton. It demonstrates the expected directory structure:

- `src/main/webapp/WEB-INF/web.xml` → registers `DispatcherServlet`
- `src/main/webapp/WEB-INF/views/*` → JSP views (server-protected)
- `src/main/java/*` → controllers/services/config (to be filled in)

## Build
```bash
cd core-spring-mvc
mvn package -DskipTests
```
The WAR will be created under:
- `target/core-spring-mvc.war`

## Deploy
Deploy the WAR to a servlet container like **Tomcat** (or any Java EE compatible container).

## Notes
This repo also contains learning docs at:
- `../../DOCS/CORE_SPRING_MVC_DIRECTORY_STRUCTURE.md`

