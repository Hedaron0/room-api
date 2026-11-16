# room-api

| Method | Path              | Status      | Description                                  |
|--------|-------------------|-------------|-----------------------------------------------|
| GET    | /api/rooms        | 200         | List all rooms, optional `minCapacity` and `keyword` query filters |
| GET    | /api/rooms/{id}   | 200/404     | Get one room by id, 404 if it doesn't exist  |
| POST   | /api/rooms        | 201/400     | Create a room, returns it with a `Location` header pointing to `/api/rooms/{id}`. 400 if the name is blank or capacity isn't 1-20 |
| PUT    | /api/rooms/{id}   | 200/404/400 | Replace a room's name/capacity, 404 if it doesn't exist, 400 if capacity isn't 1-20 |
| DELETE | /api/rooms/{id}   | 204/404/409 | Delete a room, 404 if it doesn't exist, 409 if it still has reservations |
| GET    | /api/stats        | 200         | Number of rooms, e.g. `{"rooms": 3}` |
| GET    | /api/rooms/{id}/reservations | 200/404 | Reservations of one room, ordered by start time, 404 if the room doesn't exist |
| POST   | /api/rooms/{id}/reservations | 201/400/404/409 | Book a room, `Location: /api/reservations/{id}`. 400 bad fields or end before start, 404 no room, 409 overlap |
| GET    | /api/reservations?reservedBy= | 200/400 | One person's reservations with room names (one JOIN FETCH), 400 if `reservedBy` is missing |
| GET    | /api/reservations/{id} | 200/404 | One reservation |
| DELETE | /api/reservations/{id} | 204/404 | Cancel a reservation |
| POST   | /api/auth/signup  | 201/400/409 | Create an account, returns id, username, role (never the password) |
| POST   | /api/auth/login   | 200/400/401 | Log in, returns a JWT access token |
| GET    | /api/auth/me      | 200/401     | Who is logged in |

JDK 21.

```
./gradlew bootRun
```

## Database setup

The app runs on MySQL (`roomdb`, user `roomapp`, password `roomapp1234`) through JPA. Log in to MySQL as root and run:

```sql
CREATE DATABASE roomdb;
CREATE USER 'roomapp'@'localhost' IDENTIFIED BY 'roomapp1234';
GRANT ALL PRIVILEGES ON roomdb.* TO 'roomapp'@'localhost';
```

Then create the tables and sample data (3 rooms, 5 reservations, 3 accounts) with `reset.sql`:

```
mysql -u roomapp -p roomdb < reset.sql
```

Run `reset.sql` before every `api.http` run; the expected results assume that starting state.

## Tables

**room**

| Column   | Type         | Notes                              |
|----------|--------------|------------------------------------|
| id       | BIGINT       | primary key, generated             |
| name     | VARCHAR(100) | required                           |
| capacity | INT          | required, CHECK between 1 and 20   |

**reservation**

| Column      | Type        | Notes                                    |
|-------------|-------------|------------------------------------------|
| id          | BIGINT      | primary key, generated                   |
| room_id     | BIGINT      | required, foreign key to `room.id`       |
| reserved_by | VARCHAR(50) | required                                 |
| start_time  | DATETIME    | required                                 |
| end_time    | DATETIME    | required                                 |

**app_user**

| Column        | Type         | Notes                               |
|---------------|--------------|-------------------------------------|
| id            | BIGINT       | primary key, generated              |
| username      | VARCHAR(20)  | required, unique                    |
| password_hash | VARCHAR(100) | BCrypt hash, the password is never stored |
| role          | VARCHAR(20)  | USER or ADMIN                       |

Relationship: one room has many reservations (1 : N). The foreign key lives on the "many" side, so `reservation.room_id` points to `room.id`, and `room` does not keep a list of reservations.

## 409 Conflict

`DELETE /api/rooms/1` gives 409 because room 1 still has reservations. The service checks it and throws `ConflictException`.

## PUT

PUT uses dirty checking now. `update` in `RoomService` is `@Transactional`, it loads the room and changes it, no `save()`. Hibernate writes the UPDATE at commit only if something changed.

Request 26 (same values) only does a select. Request 27:

```sql
select r1_0.id, r1_0.capacity, r1_0.name from room r1_0 where r1_0.id=?
update room set capacity=?, name=? where id=?
```

## Errors

All errors return the same body (`ApiError`), made in `ApiExceptionHandler`.

```java
public record ApiError(int status, String error, String message, String path,
                       List<FieldMessage> fieldErrors)
```

`fieldErrors` is only filled when `@Valid` fails.

Example (request 29, capacity 0):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "request has invalid fields",
  "path": "/api/rooms",
  "fieldErrors": [
    { "field": "capacity", "message": "capacity must be between 1 and 20" }
  ]
}
```

| Status | Meaning |
|--------|---------|
| 400 | wrong input: invalid field, start not before end, bad JSON, wrong id type, missing parameter |
| 401 | not logged in, wrong username or password, or a broken/expired token |
| 404 | room, reservation or URL not found |
| 409 | room already booked at that time, or deleting a room that has reservations |
| 500 | server bug, only "unexpected server error" is returned |

My own requests: 57 and 58 in `api.http`.

## Authentication

Public: all `GET /api/rooms/**`, `GET /api/stats`, `POST /api/auth/signup`, `POST /api/auth/login`.
Everything else needs a login (all POST/PUT/DELETE, `GET /api/reservations/**`, `GET /api/auth/me`).

Get a token with `POST /api/auth/login` and `{ "username": "mina", "password": "mina1234" }`.
Send it as `Authorization: Bearer <accessToken>` (lasts 30 min). HTTP Basic also works.

The JWT secret comes from the `JWT_SECRET` environment variable (there is a default for class).

Sample accounts from `reset.sql`: mina / mina1234, omar / omar1234, admin / admin1234 (ADMIN).

The queries for Assignment 3 are in `queries.sql`.
AI use: got help from Claude while writing and understanding the code.


