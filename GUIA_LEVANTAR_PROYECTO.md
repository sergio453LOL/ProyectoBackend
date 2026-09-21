# Guía para levantar el proyecto (RentEquip Backend)

Esta guía explica cómo dejar el backend corriendo localmente, desde cero, usando Docker.

## Requisitos previos

- **Docker Desktop** instalado y corriendo (incluye Docker Engine + Docker Compose).
- **IntelliJ IDEA** (opcional, solo si quieres correr/depurar el backend directamente desde el IDE en vez de en un contenedor).
- **JDK 21** instalado si vas a correrlo desde IntelliJ (no hace falta si solo usas Docker).

## Opción A — Todo con Docker (recomendado, más simple)

Esta opción levanta la base de datos PostgreSQL **y** el backend dentro de contenedores, sin instalar nada más.

1. **Abre Docker Desktop** y espera a que el ícono de la ballena en la barra de tareas quede quieto (indica que el motor ya está listo). Puedes confirmarlo también corriendo:
   ```bash
   docker ps
   ```
   Si no da error, Docker está listo.

2. **Ubícate en la carpeta del proyecto** (donde está el `pom.xml` y el `docker-compose.yml`):
   ```bash
   cd backend/backend
   ```

3. **Levanta todo el stack** (construye la imagen del backend la primera vez y arranca ambos contenedores):
   ```bash
   docker compose up -d --build
   ```

4. **Verifica que ambos contenedores estén corriendo:**
   ```bash
   docker compose ps
   ```
   Deberías ver `rentequip-postgres` y `rentequip-backend` como `Up`/`healthy`.

5. **Revisa los logs del backend** si algo no arranca:
   ```bash
   docker compose logs -f backend
   ```

6. **Abre el Swagger UI** en el navegador para confirmar que la API responde:
   ```
   http://localhost:8080/swagger-ui.html
   ```

7. **Para detener todo** (sin borrar los datos de la base):
   ```bash
   docker compose down
   ```
   Si además quieres borrar los datos de Postgres (empezar de cero):
   ```bash
   docker compose down -v
   ```

## Opción B — Solo la base de datos en Docker, backend desde IntelliJ

Útil si quieres depurar el backend con breakpoints en el IDE.

1. **Abre Docker Desktop** y espera a que esté listo.

2. **Levanta solo el contenedor de PostgreSQL** (sin el backend):
   ```bash
   cd backend/backend
   docker compose up -d db
   ```
   Esto deja Postgres escuchando en `localhost:5432`, con la base `rentequip`, usuario `postgres` y contraseña `postgres` (igual que en [`application.properties`](src/main/resources/application.properties)).

3. **Abre el proyecto en IntelliJ IDEA** (`File > Open` y selecciona la carpeta `backend/backend`, la que tiene el `pom.xml`).

4. **Espera a que IntelliJ indexe y descargue las dependencias de Maven** (barra de progreso abajo a la derecha).

5. **Corre `BackendApplication.java`** (clic derecho → `Run 'BackendApplication'`, o el botón ▶ verde junto al `main`).

6. Si ves el error **"Port 8080 was already in use"**, significa que ya hay otra instancia del backend corriendo (por ejemplo, el contenedor `rentequip-backend` de la Opción A). Detén una de las dos:
   ```bash
   docker compose stop backend
   ```

7. Confirma que responde en:
   ```
   http://localhost:8080/swagger-ui.html
   ```

## Variables de entorno (opcional)

Por defecto, la app usa estos valores (definidos en `application.properties` con fallback):

| Variable       | Valor por defecto                          |
|----------------|---------------------------------------------|
| `DB_URL`       | `jdbc:postgresql://localhost:5432/rentequip` |
| `DB_USERNAME`  | `postgres`                                   |
| `DB_PASSWORD`  | `postgres`                                   |
| `PORT`         | `8080`                                       |

Si corres el backend en Docker Compose (Opción A), estas variables ya se sobreescriben automáticamente en `docker-compose.yml` para que el backend hable con el contenedor `db` en vez de `localhost`.

## Problemas comunes

- **`Connection to localhost:5432 refused`** → Postgres no está corriendo. Levántalo con `docker compose up -d db`.
- **`Port 8080 was already in use`** → hay dos instancias del backend corriendo a la vez (IntelliJ + Docker, o dos procesos de Maven). Detén una.
- **Docker Desktop no arranca / `docker ps` falla** → abre la aplicación Docker Desktop manualmente y espera a que el motor (engine) quede activo antes de correr cualquier comando `docker`.
