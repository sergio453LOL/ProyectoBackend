# 00 — Chat madre / Coordinación (este documento)

Este no es un brief de ejecución como el 01–05. Es el **chat de control**: el que mantiene
la visión completa del proyecto contra la rúbrica, decide prioridades, resuelve dudas de
alcance entre briefs y hace la evaluación final antes de entregar. No toca código ni abre
PRs — coordina a los cinco chats que sí lo hacen.

Fecha límite de la entrega: **viernes 25 de septiembre, 11:59 p. m.**

## Rol de este chat

- Es la única fuente de verdad sobre **qué vale cuánto** en la rúbrica y en qué orden atacarlo.
- Recibe el estado actualizado de cada brief (vía el doc "Estado del Backend RentEquip" u otro
  reporte) y ajusta el plan si algo cambia — por ejemplo, si un brief tarda más de lo estimado
  o si aparece un conflicto de archivos no previsto.
- Hace la re-evaluación estricta contra la rúbrica una vez que los 5 briefs estén mergeados,
  antes de la entrega final.
- No edita `entities/`, `dtos/`, `mappers/`, `repositories/`, `exceptions/`, ni ningún otro
  archivo del repo directamente: eso es trabajo de los chats 01–05 en Claude Code.

## Mapa de puntos pendientes (referencia rápida)

| Prioridad | Brief | Puntos rúbrica | Esfuerzo | Bloquea a |
|---|---|---|---|---|
| 1 | `01-postman-collection.md` | Entregable obligatorio | ~2 h | — |
| 2 | `02-deployment.md` | 1.0 (Railway/Render) / 2.0 (AWS) | 1–5 h | — |
| 3 | `03-github-actions-y-projects.md` | 0.2 + bonus CI/CD | ~2 h | — |
| 4 | `04-eventos-async-y-correo.md` | 2.0 | ~5 h | — |
| 5 | `05-jwt-auth-y-roles.md` | 3.0 | ~8 h | Debe mergear **último** |

Puntaje base ya asegurado (secciones 1–4 de la rúbrica): **8.8 / 9**.
Puntaje hoy: **11.6 / 20**. Techo si se completa todo: **19.8–20 / 20**.

## Orden de merge recomendado

1. `03` (limpieza + CI) — sin riesgo, sin dependencias.
2. `01` (Postman) y `02` (deployment) — en paralelo, sin conflicto entre sí.
3. `04` (eventos/async) — modifica `pom.xml` y `services/`.
4. `05` (JWT) — al final. Modifica `pom.xml` y `SecurityConfig.java` (resolver conflicto
   con `02` y `04` ahí). Al cerrar las rutas, la colección de Postman (`01`) deja de servir
   hasta que se le añada el flujo de token — actualizarla justo después de este merge.

## Colisiones de archivos conocidas

| Archivo | Briefs que lo tocan | Cómo resolver |
|---|---|---|
| `pom.xml` | 04 (Thymeleaf) y 05 (librerías JWT) | El que mergea segundo hace el rebase y resuelve el conflicto de dependencias |
| `config/SecurityConfig.java` | 02 (CORS de producción) y 05 (filtro JWT y reglas de rutas) | 05 mergea al final y reincorpora el cambio de CORS de 02 |
| `README.md` | Todos, pero solo su propia sección | Cada brief edita únicamente el bloque que le corresponde |

## Checklist de cierre (antes de entregar)

- [ ] `./mvnw test` pasa completo, incluidos `ReservationConcurrencyTest` y `EquipmentSearchTest`
- [ ] `postman_collection.json` en la raíz, actualizado con el flujo de JWT
- [ ] Backend desplegado y accesible públicamente; link en el README
- [ ] README.md entre 1000 y 2000 palabras, con todas las secciones que pide el enunciado
- [ ] `.github/` con Actions corriendo tests en cada push/PR
- [ ] Historial con ramas por feature y PRs con al menos una review cada uno
- [ ] Ningún secreto (`JWT_SECRET`, credenciales, `.env`) commiteado
- [ ] Re-evaluación final contra la rúbrica hecha en este chat, con puntaje por sección

## Cómo reportar estado a este chat

Cada chat de ejecución (01–05), al cerrar su tarea, debería resumir aquí (o en el doc de
estado compartido):
1. Qué rama abrió y si ya se mergeó.
2. Qué puntos de la rúbrica cubre y con qué evidencia (comando de verificación, archivo, test).
3. Qué quedó pendiente o qué colisión encontró que no estaba prevista.

Con eso, este chat actualiza el plan y decide si hay que reordenar prioridades.
