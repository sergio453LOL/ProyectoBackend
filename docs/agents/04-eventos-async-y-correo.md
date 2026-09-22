# [04] Eventos, procesamiento asíncrono y servicio de correo

> **Puntos de rúbrica:** 2.0 — sección 7 (7.1 eventos 1.0 / 7.2 @Async 0.5 / 7.3 correo 0.5)
> **Esfuerzo estimado:** ~5 h
> **Rama:** `feat/events-async`

## Objetivo

Desacoplar las notificaciones del flujo de reserva mediante eventos de dominio publicados
tras el commit, procesados de forma asíncrona, que envían correos HTML con plantilla.

## Estado actual del código

- **No existe** ningún `@EventListener`, `@Async` ni servicio de correo.
- `pom.xml` ya declara `spring-boot-starter-mail` y `spring-boot-starter-mail-test`.
  **No** declara Thymeleaf: hay que añadir `spring-boot-starter-thymeleaf`.
- `src/main/java/com/rentequip/backend/services/ReservationService.java` tiene los tres
  puntos donde hay que publicar:
  - `create(...)` — reserva solicitada
  - `updateStatus(...)` / `confirm(...)` — cambio de estado y confirmación
  - `cancel(...)` — cancelación
  Los tres son `@Transactional(isolation = READ_COMMITTED)`.
- `ReservationStatusPolicy` decide qué transiciones son válidas; **no la toques**, solo
  léela para saber qué estados existen.
- La entidad `Company` tiene `email`; la entidad `User` también. Para las notificaciones
  usa el correo de la **empresa** (`reservation.getRenter().getEmail()` y
  `reservation.getEquipment().getOwner().getEmail()`).

## Alcance

1. Crear el paquete `com.rentequip.backend.events` con tres eventos inmutables (records o
   clases finales), cada uno cargando solo IDs y los datos mínimos:
   - `ReservationCreatedEvent`
   - `ReservationConfirmedEvent`
   - `ReservationCancelledEvent`
2. Publicarlos desde `ReservationService` con `ApplicationEventPublisher` inyectado por
   constructor (el servicio ya usa `@RequiredArgsConstructor`).
3. Crear `com.rentequip.backend.config.AsyncConfig` con `@EnableAsync` y un
   `ThreadPoolTaskExecutor` configurado explícitamente (core, max, queue capacity y un
   prefijo de nombre de hilo reconocible en los logs).
4. Crear los listeners con
   `@TransactionalEventListener(phase = AFTER_COMMIT)` **y** `@Async`. El orden importa:
   si el listener corre antes del commit y la transacción hace rollback, el correo anuncia
   una reserva que no existe.
5. Crear `EmailService` con `JavaMailSender` + plantillas Thymeleaf en
   `src/main/resources/templates/`. Mínimo dos plantillas HTML: reserva solicitada y
   reserva confirmada.
6. **Manejo de errores obligatorio**: un fallo de SMTP no puede tumbar nada. Captura la
   excepción dentro del listener, regístrala con SLF4J y sigue. La reserva ya está
   confirmada; el correo es best-effort.
7. Configurar SMTP por variables de entorno en `application.properties`
   (`MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`), **nunca** credenciales
   en el repositorio. En `src/test/resources/application.properties`, desactivar el envío
   real para que los tests no intenten conectarse a un SMTP.
8. Tests: verificar que al crear una reserva se publica el evento correspondiente
   (`@RecordApplicationEvents` + `ApplicationEvents`, o un listener de prueba).

## Por qué asíncrono (para la sección 7 del informe)

La verificación de disponibilidad **debe** seguir siendo sincrónica: el usuario necesita
saber en la misma respuesta si obtuvo la máquina. Lo que se vuelve asíncrono es el trabajo
posterior al hecho consumado — notificar —, porque la latencia del servidor SMTP no tiene
por qué sumarse al tiempo de respuesta del endpoint de reserva. Este razonamiento ya está
escrito en el `README.md`, sección 8; actualízalo cuando termines para quitar la nota de
"pendiente".

## Archivos

**Posee:**
- `src/main/java/com/rentequip/backend/events/**` (nuevo)
- `src/main/java/com/rentequip/backend/config/AsyncConfig.java` (nuevo)
- `src/main/java/com/rentequip/backend/services/EmailService.java` (nuevo)
- `src/main/java/com/rentequip/backend/services/ReservationService.java` (solo para publicar)
- `src/main/resources/templates/**` (nuevo)
- bloque de correo de `src/main/resources/application.properties`
- sección 8 del `README.md`

**Solo lectura:** `controllers/`, `entities/`, `repositories/`, `mappers/`, `dtos/`,
`ReservationStatusPolicy.java`, `SecurityConfig.java`.

**Coordinación:** añadir Thymeleaf al `pom.xml` choca con el brief 05, que también lo
modifica. Avisa antes de tocarlo.

## Criterios de aceptación

- [ ] Existen 3 eventos de dominio y se publican en los 3 puntos del flujo.
- [ ] Los listeners son `@TransactionalEventListener(AFTER_COMMIT)` + `@Async`.
- [ ] `AsyncConfig` define un `ThreadPoolTaskExecutor` propio, no el ejecutor por defecto.
- [ ] Los correos son HTML con plantilla Thymeleaf, no texto plano concatenado.
- [ ] Un fallo de SMTP deja la reserva intacta y solo registra un warning.
- [ ] `./mvnw test` pasa completo, sin conectarse a ningún SMTP real.

## Verificación

```bash
./mvnw test
docker compose up -d --build
# crear una reserva y comprobar en los logs que el listener corre en un hilo del pool
docker compose logs -f backend
```

## Trampas conocidas

- `@Async` **no funciona** en llamadas dentro de la misma clase: el proxy de Spring solo
  intercepta llamadas que entran desde fuera. El listener tiene que estar en un bean
  distinto del que publica.
- Dentro de un listener `AFTER_COMMIT` la transacción ya cerró: si accedes a una relación
  `LAZY` de una entidad, revienta con `LazyInitializationException`. Por eso los eventos
  deben llevar los datos ya extraídos, no entidades.
- `ReservationConcurrencyTest` levanta hilos en paralelo. Si tu pool de hilos o tus
  listeners introducen estado compartido mutable, ese test empezará a fallar de forma
  intermitente. Es una señal de un bug real, no de un test malo.
