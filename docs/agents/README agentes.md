# Coordinación multiagente — RentEquip Backend

Esta carpeta contiene un **brief por frente de trabajo**. Cada archivo es autocontenido:
un agente (o una persona) puede abrirlo sin más contexto y ejecutarlo de principio a fin.

Fecha límite de la entrega: **viernes 25 de septiembre, 11:59 p. m.**

## Tareas

| # | Brief | Puntos | Esfuerzo | Depende de |
|---|---|---|---|---|
| 01 | [Colección de Postman](01-postman-collection.md) | Entregable obligatorio | ~2 h | — |
| 02 | [Deployment](02-deployment.md) | 1.0 (Railway) / 2.0 (AWS) | 1–5 h | — |
| 03 | [GitHub Actions y Projects](03-github-actions-y-projects.md) | 0.2 + bonus | ~2 h | — |
| 04 | [Eventos, @Async y correo](04-eventos-async-y-correo.md) | 2.0 | ~5 h | — |
| 05 | [JWT, login y roles](05-jwt-auth-y-roles.md) | 3.0 | ~8 h | — |

Las cinco son **paralelizables**: ningún brief necesita que otro termine primero.

## Regla de oro: propiedad de archivos

Para que cinco agentes trabajen a la vez sin pisarse, cada brief **posee** un conjunto de
archivos y **nadie más los toca**. Si un brief necesita un cambio fuera de su zona, lo
anota en su PR y lo coordina; no lo hace por su cuenta.

| Zona | Dueño |
|---|---|
| `postman_collection.json` | 01 |
| `Dockerfile`, `docker-compose.yml`, `application.properties` (bloque de deploy) | 02 |
| `.github/**` | 03 |
| `services/`, `events/` (nuevo), `config/AsyncConfig.java` | 04 |
| `config/SecurityConfig.java`, `security/` (nuevo), `controllers/`, `pom.xml` | 05 |
| `README.md` | nadie sin avisar — lo actualiza quien cierre su tarea, solo en su sección |

Zona compartida de solo lectura para todos: `entities/`, `dtos/`, `mappers/`,
`repositories/`, `exceptions/`. Si tu tarea parece exigir cambiarlos, párate y consulta.

## Flujo de trabajo

1. Crear rama desde `main`: `git checkout -b feat/<tema>` (ej. `feat/jwt-auth`).
2. Commits en formato Conventional Commits, igual que el historial existente
   (`feat(security): ...`, `test: ...`, `chore: ...`).
3. Antes de abrir el PR: `./mvnw test` tiene que pasar **completo**, incluidos
   `ReservationConcurrencyTest` y `EquipmentSearchTest`. Si tu cambio los rompe, el
   problema es tu cambio.
4. Pull request a `main` con al menos una review de otro integrante.
5. Al mergear, marcar la tarea como cerrada en el tablero de GitHub Projects (ver brief 03).

La rúbrica premia explícitamente ramas por feature, PRs con code review e historial limpio
(sección 9.2, 0.4 pts). Trabajar así **es** parte de la nota, no burocracia.

## Estado del proyecto (contexto común)

Lo que ya está hecho y **no hay que rehacer**: 7 entidades JPA con relaciones, índices y
validaciones; 21 DTOs y 7 mappers; arquitectura Controller → Service → Repository; 9
excepciones personalizadas con `GlobalExceptionHandler`; 6 controladores REST bajo
`/api/v1`; reserva transaccional con lock pesimista probada bajo concurrencia; búsqueda
geoespacial con Haversine; Swagger, Docker Compose y logging SLF4J.

Stack: Java 21, Spring Boot 4.1.1, PostgreSQL 16, Maven (wrapper incluido).
Los tests corren sobre H2 en modo PostgreSQL, sin contenedores: `./mvnw test`.

## Plantilla

Para añadir un frente nuevo, copiar [`_PLANTILLA.md`](_PLANTILLA.md).
