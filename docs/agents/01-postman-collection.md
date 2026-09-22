# [01] Colección de Postman

> **Puntos de rúbrica:** entregable obligatorio (sin él, la entrega queda incompleta)
> **Esfuerzo estimado:** ~2 h
> **Rama:** `feat/postman-collection`

## Objetivo

Que exista `postman_collection.json` en la **raíz del repositorio**, con todos los endpoints
del proyecto, variables definidas, autorización configurada y ejemplos de respuesta.

## Estado actual del código

- No existe ninguna colección en el repositorio.
- La API ya expone su especificación OpenAPI en `http://localhost:8080/v3/api-docs`
  (configurado en `src/main/resources/application.properties`).
- Los endpoints son los 6 controladores de `src/main/java/com/rentequip/backend/controllers/`.
- Cinco de ellos exigen la cabecera `X-Company-Id` en las operaciones sensibles
  (constante `ACTING_COMPANY_HEADER` en cada controlador).

## Alcance

1. Levantar el backend: `docker compose up -d --build`.
2. Importar `http://localhost:8080/v3/api-docs` en Postman como punto de partida. **No**
   entregar esa importación tal cual: hay que organizarla y documentarla.
3. Organizar en carpetas por recurso: Companies, Users, Equipment Categories, Equipment,
   Reservations, Reviews.
4. Definir variables de colección: `baseUrl` (`http://localhost:8080/api/v1`),
   `companyId`, `equipmentId`, `reservationId`, `token` (para cuando exista JWT).
   Ningún request debe llevar una URL ni un ID escritos a mano.
5. Configurar la autorización a nivel de colección (pestaña Authorization). Mientras no
   exista JWT, poner la cabecera `X-Company-Id: {{companyId}}` como header de colección.
   Cuando el brief 05 termine, cambiar a Bearer Token con `{{token}}`.
6. Cada request necesita: **descripción** de para qué sirve y por qué importa, un body de
   ejemplo válido, y al menos un **ejemplo de respuesta guardado** (Save as example).
7. Incluir los casos de error que demuestran la lógica de negocio, no solo los felices:
   - reserva con fechas solapadas → `409 OVERBOOKING`
   - reserva sobre equipo propio → `400`
   - `PATCH` de reserva con `X-Company-Id` ajeno → `403`
   - creación con body inválido → `400` con la lista de campos
8. Añadir scripts de test básicos en los requests de creación para encadenar el flujo:
   guardar el `id` de la respuesta en la variable de colección correspondiente.
9. Exportar como **Collection v2.1** y guardar en la raíz como `postman_collection.json`.

## Flujo completo que la colección debe permitir ejecutar de corrido

Crear empresa A -> crear empresa B -> crear usuario en A -> crear categoría ->
crear equipo (dueño A) -> buscar por proximidad -> consultar disponibilidad ->
crear reserva (arrendatario B) -> intentar reserva solapada y obtener 409 ->
confirmar como A -> pasar a IN_PROGRESS -> completar -> crear reseña como B ->
consultar promedio de rating.

## Archivos

**Posee:** `postman_collection.json`

**Solo lectura:** todo lo demás. Esta tarea no modifica código Java.

## Criterios de aceptación

- [ ] `postman_collection.json` está en la raíz del repositorio.
- [ ] Ningún request tiene URLs o IDs hardcodeados; todo usa variables.
- [ ] Todos los endpoints de los 6 controladores están cubiertos.
- [ ] Cada request tiene descripción y al menos un ejemplo de respuesta guardado.
- [ ] El flujo completo de arriba corre de principio a fin con Collection Runner sobre una
      base de datos limpia.

## Verificación

```bash
docker compose down -v && docker compose up -d --build
# esperar a que el backend arranque, luego correr la colección con el Runner
```

## Trampas conocidas

- Las fechas de reserva **no pueden estar en el pasado** (`validateDateRange` en
  `ReservationService`), así que no dejes fechas fijas de septiembre: usa un script de
  pre-request que calcule fechas futuras.
- El dueño de un equipo no puede alquilárselo a sí mismo: necesitas **dos** empresas.
- Una reseña solo se puede crear sobre una reserva en estado `COMPLETED`, y solo por el
  arrendatario.
