# room-api

| Method | Path              | Status      | Description                                  |
|--------|-------------------|-------------|-----------------------------------------------|
| GET    | /api/rooms        | 200         | List all rooms, optional `minCapacity` and `keyword` query filters |
| GET    | /api/rooms/{id}   | 200/404     | Get one room by id, 404 if it doesn't exist  |
| POST   | /api/rooms        | 201/400     | Create a room, returns it with a `Location` header pointing to `/api/rooms/{id}`. 400 if capacity isn't 1-20 |
| PUT    | /api/rooms/{id}   | 200/404/400 | Replace a room's name/capacity, 404 if it doesn't exist, 400 if capacity isn't 1-20 |
| DELETE | /api/rooms/{id}   | 204/404     | Delete a room, 404 if it doesn't exist       |

JDK 21.

```
./gradlew bootRun
```

## Database

The app starts an in-memory H2 database (`jdbc:h2:mem:roomdb`, MySQL mode). `schema.sql` and `data.sql` in `src/main/resources` run at every startup and create the tables with 3 rooms and 5 reservations. The API itself still uses the in-memory `List` repository; it switches to the database in Week 6.

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

Relationship: one room has many reservations (1 : N). The foreign key lives on the "many" side, so `reservation.room_id` points to `room.id`, and `room` does not keep a list of reservations.

H2 console: run the app, then open http://localhost:8080/h2-console and connect with

| Field      | Value                |
|------------|----------------------|
| JDBC URL   | `jdbc:h2:mem:roomdb` |
| User Name  | `sa`                 |
| Password   | (empty)              |

The queries for Assignment 3 are in `queries.sql`.

AI use: got help from Claude while writing and understanding the code.
