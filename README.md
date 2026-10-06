# room-api

| Method | Path              | Status      | Description                                  |
|--------|-------------------|-------------|-----------------------------------------------|
| GET    | /api/rooms        | 200         | List all rooms, optional `minCapacity` and `keyword` query filters |
| GET    | /api/rooms/{id}   | 200/404     | Get one room by id, 404 if it doesn't exist  |
| POST   | /api/rooms        | 201/400     | Create a room, returns it with a `Location` header pointing to `/api/rooms/{id}`. 400 if capacity isn't 1-20 |
| PUT    | /api/rooms/{id}   | 200/404/400 | Replace a room's name/capacity, 404 if it doesn't exist, 400 if capacity isn't 1-20 |
| DELETE | /api/rooms/{id}   | 204/404/409 | Delete a room, 404 if it doesn't exist, 409 if it still has reservations |
| GET    | /api/stats        | 200         | Number of rooms, e.g. `{"rooms": 3}` |

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

Then create the tables and sample data (3 rooms, 5 reservations) with `reset.sql`:

```
mysql -u roomapp -p roomdb < reset.sql
```

Run `reset.sql` before every `api.http` run; the expected results assume that starting state.
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

## 409 Conflict

`DELETE /api/rooms/1` answers 409 because the foreign key from `reservation` to `room` blocks it: Room 1 has reservations. The controller returns it, because it is the only layer that knows about HTTP status codes; the service and repository just let the `DataIntegrityViolationException` pass up.

## PUT

Hibernate printed this for request 8 (`PUT /api/rooms/1`):

```sql
select r1_0.id, r1_0.capacity, r1_0.name from room r1_0 where r1_0.id=?
update room set capacity=?, name=? where id=?
```

The `select` is `findById` checking that the room exists (404 if not), and the `update` is `save` writing the new values, because the object has an id.

The queries for Assignment 3 are in `queries.sql`.
AI use: got help from Claude while writing and understanding the code.


