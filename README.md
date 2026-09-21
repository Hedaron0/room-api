# room-api

A small REST API for managing a list of rooms (name + capacity) that can be searched, created, updated, and deleted.

## Endpoints

| Method | Path              | Status  | Description                                  |
|--------|-------------------|---------|-----------------------------------------------|
| GET    | /api/rooms        | 200     | List all rooms, optional `minCapacity` and `keyword` query filters |
| GET    | /api/rooms/{id}   | 200/404 | Get one room by id, 404 if it doesn't exist  |
| POST   | /api/rooms        | 201     | Create a room, returns it with a `Location` header pointing to `/api/rooms/{id}` |
| PUT    | /api/rooms/{id}   | 200/404 | Replace a room's name/capacity, 404 if it doesn't exist |
| DELETE | /api/rooms/{id}   | 204/404 | Delete a room, 404 if it doesn't exist       |

## How to run

```
./gradlew bootRun
```

The server starts on `http://localhost:8080`. See `api.http` for a request against every endpoint above.
