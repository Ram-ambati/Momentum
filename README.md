# Momentum

Momentum is a Spring Boot backend for a gamified productivity app with immutable task history, XP/coin ledgers, rewards, levels, streaks, and timezone-aware daily views.

## Run

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

Configure PostgreSQL via env vars:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`

## API (MVP)

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `POST /api/tasks`
- `GET /api/tasks/today`
- `POST /api/tasks/{id}/complete`
- `PATCH /api/tasks/{id}/status`
- `GET /api/tasks/history/{date}`
- `GET /api/history?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `GET /api/progress`
- `GET /api/streaks`
- `GET /api/transactions/xp`
- `GET /api/transactions/coins`
- `GET /api/rewards`
- `POST /api/rewards`
- `POST /api/rewards/{id}/redeem`
- `GET /api/rewards/redemptions`
