# Turnero — appointment scheduling system

[![CI](https://github.com/MatiasMedina846/Turnero/actions/workflows/ci.yml/badge.svg)](https://github.com/MatiasMedina846/Turnero/actions/workflows/ci.yml)

**EN** — Full-stack MVP to manage appointments for a small business (barbershop, nail salon...). Its core rule: **an employee can never be double-booked**. The overlap check and the insert run inside one transaction with a row lock on the employee, so two simultaneous requests for the same time slot cannot both succeed.

**ES** — MVP full-stack para gestionar los turnos de un negocio chico (barbería, manicuría...). Su regla central: **un empleado nunca puede quedar con dos turnos superpuestos**. La verificación de solapamiento y el guardado ocurren en una misma transacción con un bloqueo de fila sobre el empleado, así que dos pedidos simultáneos para el mismo horario no pueden ganar los dos.

## Stack

| Layer | Technology |
|-------|------------|
| Backend | Java 21, Spring Boot, Spring Data JPA, Bean Validation, Maven |
| Database | PostgreSQL (H2 in memory for tests) |
| Frontend | Angular 21 (standalone components) |
| Quality | JUnit 5, Mockito, MockMvc, GitHub Actions |

## Features / Funcionalidades

- Create, edit, list and delete appointments (`/api/turnos`). / Alta, edición, listado y baja de turnos.
- The end time is calculated from the service duration. / La hora de fin se calcula con la duración del servicio.
- Overlap detection per employee, back-to-back appointments are allowed. / Detección de solapamientos por empleado; se permiten turnos consecutivos.
- Employees and services catalog (`/api/empleados`, `/api/servicios`) with seed data on first run. / Catálogo de empleados y servicios con datos iniciales.
- Clear HTTP errors: `400` invalid body, `404` not found, `409` employee already booked. / Errores HTTP claros: `400`, `404`, `409`.

## Architecture / Arquitectura

```
controller  →  service  →  repository  →  PostgreSQL
   (REST/DTO)   (business rules,   (Spring Data JPA)
                 @Transactional)
exception/GlobalExceptionHandler  →  { "error": "..." } JSON responses
```

Concurrency: `TurnoService.crear/actualizar` load the employee with `SELECT ... FOR UPDATE` (`EmpleadoRepository.findByIdForUpdate`) before checking overlaps, which serializes bookings of the same employee.

## Live demo / Demo en vivo

- Frontend: https://matiasmedina846.github.io/Turnero/
- API health: https://turnero-api-viu8.onrender.com/actuator/health

The backend runs on Render's free plan, so the first request after a period of inactivity can take about a minute while it wakes up.
El backend corre en el plan gratuito de Render: la primera request tras un rato de inactividad puede tardar cerca de un minuto en despertar.

## Run it locally / Cómo correrlo

Requirements: Java 21, Node 22+, Docker (for the database).

```bash
# 1. Database
docker compose up -d

# 2. Backend (http://localhost:8080)
./mvnw spring-boot:run

# 3. Frontend (http://localhost:4200, proxies /api to the backend)
cd frontend
npm install
npm start
```

Configuration is read from environment variables (defaults match the Docker database):

| Variable | Default |
|----------|---------|
| `DB_URL` | `jdbc:postgresql://localhost:5432/turnos` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |
| `CORS_ALLOWED_ORIGIN` | `http://localhost:4200` |

To use a hosted database (Neon, Supabase...) export `DB_URL`, `DB_USER` and `DB_PASSWORD`. **Never commit credentials.**

## Deploy (Render + Neon)

The backend ships with a multi-stage `Dockerfile` and a `render.yaml` Blueprint.
El backend incluye un `Dockerfile` multi-stage y un Blueprint `render.yaml`.

1. Create a free PostgreSQL database on [Neon](https://neon.tech) and copy the connection details. / Creá una base PostgreSQL gratis en Neon.
2. In [Render](https://render.com): **New > Blueprint**, pick this repo. / En Render: **New > Blueprint** y elegí este repo.
3. Set the environment variables in the dashboard (never commit them) / Cargá las variables en el panel (nunca en el repo):

| Variable | Example / Ejemplo |
|---|---|
| `DB_URL` | `jdbc:postgresql://<neon-host>/<db>?sslmode=require` |
| `DB_USER` | Neon user / usuario de Neon |
| `DB_PASSWORD` | Neon password / contraseña de Neon |
| `CORS_ALLOWED_ORIGIN` | URL of the deployed frontend / URL del frontend desplegado |

4. Health check: `GET /actuator/health`. The free plan sleeps after inactivity, so the first request can take ~30-60 s. / El plan gratis se duerme por inactividad: la primera request puede tardar ~30-60 s.

## Tests

```bash
./mvnw test
```

- `TurnoServiceTest` — business rules with mocked repositories.
- `TurnoServiceIntegrationTest` — real service against H2: overlaps, consecutive slots, rescheduling over its own slot, and concurrent bookings of the same slot (exactly one wins).
- `TurnoControllerTest` — HTTP status codes for validation, not-found and conflict errors.

## API

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/turnos` | List appointments ordered by start time |
| GET | `/api/turnos/{id}` | Get one appointment |
| POST | `/api/turnos` | Create (`201`, or `409` if the employee is busy) |
| PUT | `/api/turnos/{id}` | Update |
| DELETE | `/api/turnos/{id}` | Delete (`204`) |
| GET / POST | `/api/empleados` | List / create employees |
| GET / POST | `/api/servicios` | List / create services |

Example body for `POST /api/turnos`:

```json
{
  "clienteNombre": "Ana",
  "clienteTelefono": "3534000000",
  "empleadoId": 1,
  "servicioId": 2,
  "fechaHoraInicio": "2030-01-15T10:00:00"
}
```

## Next steps / Próximos pasos

- Authentication and roles. / Autenticación y roles.
- Cancel appointments (status `CANCELADO`) and a calendar view. / Cancelación de turnos y vista de calendario.
- Deploy with a public demo. / Despliegue con demo pública.

---
Author: [Matias Medina](https://github.com/MatiasMedina846) · [LinkedIn](https://www.linkedin.com/in/matias-medina-german/)
