# [02] Deployment

> **Puntos de rúbrica:** 2.0 (AWS ECS/EC2 + RDS) o 1.0 (Railway/Render/Heroku) — sección 8
> **Esfuerzo estimado:** ~1 h en Railway, ~5 h en AWS
> **Rama:** `feat/deployment`

## Objetivo

Que la API esté corriendo en internet, accesible públicamente, con base de datos en la
nube y variables de entorno de producción.

## Decisión a tomar primero

La rúbrica paga **el doble** por AWS (ECS o EC2 + RDS) que por una plataforma de despliegue
instantáneo. Recomendación: hacer **Railway primero** (media hora, asegura 1.0 punto) y, si
queda tiempo, migrar a AWS. Tener algo desplegado vale más que tener AWS a medias, porque
un deployment "parcialmente funcional" solo da 0.5.

## Estado actual del código

- `Dockerfile` multi-stage ya funcional: construye con Maven y corre sobre
  `eclipse-temurin:21-jre`, expone el puerto 8080.
- `docker-compose.yml` levanta `db` (postgres:16) + `backend`, con healthcheck en la base.
  `restart: "no"` en ambos servicios (elegido a propósito para desarrollo local).
- `src/main/resources/application.properties` ya lee todo de variables de entorno con
  fallback local: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DDL_AUTO`, `PORT`.
- `spring.jpa.hibernate.ddl-auto` está en `update`. Sirve para la demo; no es lo correcto
  para producción real, pero migrar a Flyway **no** entra en esta tarea.

## Alcance — Opción A: Railway (1.0 pt)

1. Crear proyecto en Railway y conectar el repositorio de GitHub.
2. Añadir un servicio **PostgreSQL** desde el catálogo de Railway.
3. En el servicio del backend, definir las variables de entorno apuntando a la base de
   Railway: `DB_URL` (formato `jdbc:postgresql://host:puerto/base`), `DB_USERNAME`,
   `DB_PASSWORD`. Railway inyecta `PORT` automáticamente; la app ya lo respeta.
4. Verificar que el build usa el `Dockerfile` del repositorio.
5. Comprobar que `https://<dominio>/swagger-ui.html` responde.

## Alcance — Opción B: AWS (2.0 pts)

1. **RDS**: instancia PostgreSQL 16, sin acceso público, en la misma VPC que el backend.
2. **ECR**: crear repositorio y subir la imagen construida con el `Dockerfile` actual.
3. **ECS Fargate**: task definition con las variables `DB_URL`, `DB_USERNAME`,
   `DB_PASSWORD` (idealmente vía Secrets Manager, no en texto plano), y un servicio
   detrás de un Application Load Balancer.
4. **Security groups**: el de RDS solo acepta tráfico del security group de ECS en el
   puerto 5432; el del ALB acepta 80/443 desde internet.
5. Comprobar que el dominio del ALB responde en `/swagger-ui.html`.

## Tareas comunes a ambas opciones

- Añadir el enlace del deployment al `README.md`, en la línea `**Deployment:**` de la
  portada, que hoy dice "pendiente".
- Ampliar el patrón de CORS en `src/main/java/com/rentequip/backend/config/SecurityConfig.java`
  si el frontend se despliega en un dominio distinto de `localhost` o `*.vercel.app`.
  **Coordinar con el brief 05**, que también toca ese archivo.
- Verificar que las credenciales de producción **no** quedan en el repositorio.

## Archivos

**Posee:** `Dockerfile`, `docker-compose.yml`, configuración de la plataforma,
línea de deployment del `README.md`.

**Solo lectura:** todo el código Java, salvo el bean de CORS coordinado con el brief 05.

## Criterios de aceptación

- [ ] La URL pública responde `200` en `GET /api/v1/equipment-categories`.
- [ ] Swagger UI carga desde el dominio público.
- [ ] Se puede crear una empresa y recuperarla por su id contra el entorno desplegado.
- [ ] Las credenciales viven en variables de entorno de la plataforma, no en el repo.
- [ ] El enlace está en el `README.md`.

## Verificación

```bash
curl -i https://<dominio>/api/v1/equipment-categories
curl -i -X POST https://<dominio>/api/v1/companies -H "Content-Type: application/json" -d @empresa.json
```

## Trampas conocidas

- `DB_URL` es una URL **JDBC**, no la `DATABASE_URL` estilo `postgres://user:pass@host`
  que dan algunas plataformas. Hay que convertirla a mano.
- El primer arranque con `ddl-auto=update` crea el esquema; si la base arranca más lenta
  que la app, el contenedor muere. En Railway basta con reiniciar; en ECS hay que ajustar
  el health check grace period.
- La imagen construye con Maven dentro del `Dockerfile`: el primer build tarda varios
  minutos y algunas plataformas cortan por timeout. Si pasa, construir en local y subir la
  imagen ya compilada.
