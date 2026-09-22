# [03] GitHub Actions y GitHub Projects

> **Puntos de rúbrica:** 0.2 (sección 9.3) + bonus de CI/CD + sustenta la sección
> "GitHub & Management" del informe
> **Esfuerzo estimado:** ~2 h
> **Rama:** `feat/ci`

## Objetivo

Que cada push y cada pull request a `main` ejecute las pruebas automáticamente, y que el
trabajo del equipo esté organizado en un tablero con issues, labels y milestones.

## Estado actual del código

- **No existe** carpeta `.github/` en el repositorio.
- El historial tiene 10 commits en formato Conventional Commits, todos directos a `main`,
  sin ramas ni pull requests.
- Las pruebas corren con `./mvnw test` sobre **H2 en modo PostgreSQL**
  (`src/test/resources/application.properties`), así que **el CI no necesita levantar
  PostgreSQL** para que pasen los tests actuales.
- Hay 3 clases de test: `BackendApplicationTests`, `EquipmentSearchTest`,
  `ReservationConcurrencyTest`.

## Alcance — CI

1. Crear `.github/workflows/ci.yml` con un job que, en `push` y `pull_request` a `main`:
   - haga checkout,
   - configure JDK 21 (`actions/setup-java@v4`, distribución `temurin`),
   - cachee las dependencias de Maven,
   - ejecute `./mvnw -B verify`.
2. El workflow debe fallar si falla cualquier test. `ReservationConcurrencyTest` levanta
   varios hilos: si resulta inestable en el runner, **no lo desactives**; investiga y
   documenta la causa en el PR.
3. Opcional (bonus): segundo job que construya la imagen Docker al mergear a `main`,
   usando el `Dockerfile` existente.
4. Añadir el badge de estado del workflow al inicio del `README.md`.

## Alcance — Projects

1. Crear un **GitHub Project** (vista tablero) asociado al repositorio, con columnas
   `Backlog` / `En progreso` / `En review` / `Hecho`.
2. Crear un issue por cada brief de `docs/agents/` (los 5), asignado a un integrante y
   con label de tipo: `feature`, `infra`, `docs`, `test`.
3. Crear un **milestone** "Entrega Semana 7 — 25 de septiembre" y asociar los 5 issues.
4. Enlazar cada pull request a su issue (`Closes #N`) para que el tablero se mueva solo.

## Archivos

**Posee:** `.github/**`, badge del `README.md`.

**Solo lectura:** todo el código Java.

## Criterios de aceptación

- [ ] El workflow aparece en verde en la pestaña Actions sobre un commit de `main`.
- [ ] Un PR abierto muestra el check de CI antes de poder mergear.
- [ ] El tablero tiene los 5 issues, con asignado, label y milestone.
- [ ] El badge de CI se ve en el `README.md`.

## Verificación

```bash
git checkout -b test/ci-smoke && git commit --allow-empty -m "chore: probar el pipeline" && git push
# abrir un PR y comprobar que el check aparece y pasa
```

## Trampas conocidas

- En Linux hay que dar permisos al wrapper antes de usarlo: `chmod +x mvnw`. Los finales
  de línea ya están resueltos: `.gitattributes` fuerza `mvnw text eol=lf`.
- Usa `./mvnw` (el wrapper), no `mvn`: la versión de Maven queda fijada por el proyecto.
- No metas secretos en el YAML. Si el job de Docker necesita credenciales, van en
  repository secrets.
