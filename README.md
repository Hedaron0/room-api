# room-api

| Method | Path              | Status      | Description                                  |
|--------|-------------------|-------------|-----------------------------------------------|
| GET    | /api/rooms        | 200         | List all rooms, optional `minCapacity` and `keyword` query filters |
| GET    | /api/rooms/{id}   | 200/404     | Get one room by id, 404 if it doesn't exist  |
| POST   | /api/rooms        | 201/400     | Create a room, returns it with a `Location` header pointing to `/api/rooms/{id}`. 400 if capacity isn't 1-20 |
| PUT    | /api/rooms/{id}   | 200/404/400 | Replace a room's name/capacity, 404 if it doesn't exist, 400 if capacity isn't 1-20 |
| DELETE | /api/rooms/{id}   | 204/404     | Delete a room, 404 if it doesn't exist       |

JDK 24.

```
./gradlew bootRun
```

AI use: got help from Claude while writing and understanding the code.
