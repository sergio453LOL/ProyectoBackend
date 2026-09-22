# [05] JWT, registro/login y autorización por roles

> **Puntos de rúbrica:** 3.0 — sección 5 (5.1 config 1.0 / 5.2 JWT 1.5 / 5.3 roles 1.0 /
> 5.4 registro y login 0.5; hoy se obtiene casi nada de esos 4 puntos)
> **Esfuerzo estimado:** ~8 h — es la tarea más grande
> **Rama:** `feat/jwt-auth`

## Objetivo

Sustituir la identificación por cabecera `X-Company-Id` —que hoy cualquiera puede
falsificar— por autenticación real con JWT, y hacer cumplir los roles que ya existen en el
modelo.

## Estado actual del código

- `src/main/java/com/rentequip/backend/config/SecurityConfig.java`: sesión `STATELESS`,
  CSRF deshabilitado, CORS configurado, `BCryptPasswordEncoder` como bean... pero
  **todas las rutas están en `permitAll()`**. No hay filtro de autenticación.
- **No existe** paquete `security/`, ni filtro JWT, ni `UserDetailsService`, ni controlador
  de autenticación. No hay endpoint de login.
- `pom.xml` **no** tiene dependencia de JWT: hay que añadir `jjwt-api`, `jjwt-impl` y
  `jjwt-jackson` (o equivalente).
- La entidad `User` ya tiene `email` único, `password` (ya guardado con BCrypt por
  `UserService.create`), `enabled` y `role`.
- El enum `UserRole` ya define `ADMIN`, `MANAGER`, `OPERATOR`. **Nadie los verifica.**
- Existe `UnauthorizedException` en `exceptions/`, ya mapeada por
  `GlobalExceptionHandler`, y hoy no se usa desde ningún sitio. Es tuya.
- Cinco controladores leen la constante `ACTING_COMPANY_HEADER = "X-Company-Id"`:
  `CompanyController`, `EquipmentController`, `ReservationController`,
  `ReviewController`, `UserController`.
- **Dato clave:** los servicios ya validan la propiedad del recurso (`validateSameCompany`,
  `validateVisibility`, `ForbiddenOperationException.notOwner`). Esa lógica **no cambia**.
  Lo único que cambia es de dónde sale el id de la empresa que actúa.

## Alcance

1. Añadir las dependencias de JWT al `pom.xml`.
2. Crear el paquete `com.rentequip.backend.security` con:
   - `JwtService` — genera y valida tokens; claims: `userId`, `email`, `companyId`, `role`.
     El `companyId` en el token es lo que reemplaza a la cabecera.
   - `JwtAuthenticationFilter` — extrae el token de `Authorization: Bearer ...`, lo valida
     y puebla el `SecurityContext`.
   - `CustomUserDetailsService` — carga el usuario por email desde `UserRepository`.
   - Un `AuthenticationEntryPoint` que devuelva el mismo `ErrorResponse` que usa
     `GlobalExceptionHandler`, para no romper el contrato de errores.
3. Crear `AuthController` en `controllers/` con:
   - `POST /api/v1/auth/register` — alta de empresa + usuario administrador.
   - `POST /api/v1/auth/login` — devuelve access token y refresh token.
   - `POST /api/v1/auth/refresh` — renueva el access token.
   Validar fuerza de contraseña en el DTO de registro (la entidad ya exige mínimo 8).
4. Reescribir `SecurityConfig`: registrar el filtro JWT antes de
   `UsernamePasswordAuthenticationFilter`, y cambiar las reglas de rutas:
   - públicas: `/api/v1/auth/**`, `GET /api/v1/equipment/search`,
     `GET /api/v1/equipment-categories/**`, `/swagger-ui/**`, `/v3/api-docs/**`
   - el resto, autenticado.
5. Sustituir en los 5 controladores el `@RequestHeader("X-Company-Id")` por la
   resolución desde el `SecurityContext`. La forma limpia: un `HandlerMethodArgumentResolver`
   o una anotación `@ActingCompany` que inyecte el `Long` — así las firmas de los métodos
   de servicio **no cambian** y nada más se rompe.
6. Añadir `@PreAuthorize` en las operaciones sensibles (hace falta
   `@EnableMethodSecurity`). Criterio sugerido:
   - `ADMIN`: gestionar usuarios de su empresa, dar de baja la empresa.
   - `ADMIN` y `MANAGER`: publicar, editar y borrar maquinaria; confirmar y rechazar reservas.
   - `OPERATOR`: solicitar reservas, consultar, dejar reseñas.
7. `JWT_SECRET` y tiempos de expiración por **variable de entorno**, con fallback solo para
   desarrollo. Nunca una clave fija en el repositorio.
8. Configurar el esquema de seguridad Bearer en springdoc para que el botón "Authorize"
   funcione en Swagger UI.
9. Tests: login correcto devuelve token; petición sin token da 401; petición con token de
   otra empresa da 403; rol insuficiente da 403.
10. Actualizar el `README.md`: sección 7 ("Autorización actual") y la tabla de "Estado de
    la entrega".

## Archivos

**Posee:**
- `src/main/java/com/rentequip/backend/security/**` (nuevo)
- `src/main/java/com/rentequip/backend/config/SecurityConfig.java`
- `src/main/java/com/rentequip/backend/controllers/**`
- `src/main/java/com/rentequip/backend/dtos/request/` y `response/` — **solo** los DTOs
  nuevos de autenticación
- `pom.xml`
- secciones 7 y "Estado de la entrega" del `README.md`

**Solo lectura:** `services/` (salvo el nuevo `AuthService`), `entities/`, `repositories/`,
`mappers/`, `exceptions/`.

**Coordinación:** tocas `pom.xml` (choca con el brief 04) y `SecurityConfig` (CORS, choca
con el brief 02). Avisa en ambos casos.

## Criterios de aceptación

- [ ] `POST /api/v1/auth/login` con credenciales válidas devuelve access y refresh token.
- [ ] Una petición sin token a un endpoint protegido devuelve **401** con el `ErrorResponse`
      estándar del proyecto.
- [ ] Un token válido de la empresa A no permite modificar recursos de la empresa B (**403**).
- [ ] `@PreAuthorize` bloquea a un `OPERATOR` que intenta publicar maquinaria (**403**).
- [ ] La cabecera `X-Company-Id` ya no aparece en ningún controlador.
- [ ] El secreto del token sale de una variable de entorno.
- [ ] `./mvnw test` pasa completo, incluidos los tests de concurrencia preexistentes.

## Verificación

```bash
./mvnw test
docker compose up -d --build
curl -i -X POST localhost:8080/api/v1/auth/login -H "Content-Type: application/json" \
  -d '{"email":"admin@empresa.com","password":"contrasena123"}'
curl -i localhost:8080/api/v1/reservations            # espera 401
curl -i localhost:8080/api/v1/reservations -H "Authorization: Bearer <token>"
```

## Trampas conocidas

- **Este es el brief que más puede romper a los demás.** Al cerrar las rutas, la colección
  de Postman del brief 01 deja de funcionar hasta que se le añada el Bearer token, y
  cualquier test de integración que llame a los controladores empezará a dar 401. Avisa al
  equipo **antes** de mergear.
- `ReservationConcurrencyTest` y `EquipmentSearchTest` llaman a los **servicios**
  directamente, no a los controladores, así que no deberían romperse. Si se rompen, es que
  metiste lógica de seguridad en la capa equivocada.
- No dupliques las comprobaciones de propiedad que ya hacen los servicios. `@PreAuthorize`
  verifica **rol**; la pertenencia del recurso ya está resuelta y probada donde debe estar.
- El registro crea empresa y usuario a la vez: envuélvelo en `@Transactional` o te quedarás
  con empresas huérfanas si falla la creación del usuario.
