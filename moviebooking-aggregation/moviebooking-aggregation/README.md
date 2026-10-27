# Movie Booking — Spring Boot + MongoDB Aggregation

Simple Maven project using Spring Boot, MongoDB, Lombok, repositories, service layer, and REST controllers.

## Requirements
- JDK 17+
- Maven
- MongoDB running locally on port 27017

Database: `moviebooking` (configured in `src/main/resources/application.properties`).

## Run
From the project root:

```bash
mvn spring-boot:run
```

Base URL: `http://localhost:8080/api`

## Endpoints
### Movies
- `GET /api/movies` — list movies
- `POST /api/movies` — add a movie
- `PUT /api/movies/{id}` — update a movie
- `DELETE /api/movies/{id}` — delete a movie
- `GET /api/movies/genre/{genre}` — filter by genre
- `GET /api/movies/search?name=bat` — search by name

### Bookings
- `GET /api/bookings` — list bookings
- `POST /api/bookings` — create a booking
- `DELETE /api/bookings/{id}` — delete a booking
- `GET /api/movies/{movieId}/bookings` — list bookings for a movie

### Aggregation
- `GET /api/movies/with-bookings` — `$lookup`, `$match`, `$addFields`, `$project`: only movies with bookings, plus booking count and total tickets.
- `GET /api/movies/booking-summary` — `$lookup`, `$unwind`, `$group`, `$project`: booking count, total tickets, and revenue for each booked movie.

## Sample requests
Create a movie with `POST /api/movies`:
```json
{"name":"Interstellar","genre":"Sci-Fi","price":250,"duration":169}
```
Use the returned `id` as `movieId` when creating a booking with `POST /api/bookings`:
```json
{"customerName":"Sanjay","movieId":"PASTE_MOVIE_ID_HERE","tickets":2,"totalAmount":500}
```
Then call the aggregation endpoints.

## How the join works
`movies._id` is matched to `bookings.movieId`. `$lookup` adds matching bookings to a `bookingDetails` array. `$match` removes movies whose array is empty. The summary pipeline unwinds booking rows and groups them by movie to calculate counts and revenue.

Important: the booking `movieId` must match the movie document's `_id` string.
