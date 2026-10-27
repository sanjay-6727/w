# Movie Booking — Spring Boot + MongoDB (without aggregation pipelines)

This project uses Lombok and MongoRepository. It intentionally does **not** use MongoDB aggregation pipelines or DTO classes. Related documents are connected using `Booking.movieId`, and Java streams are used for cross-collection and aggregate-like calculations.

## Requirements
- JDK 17
- Maven (or IntelliJ's bundled Maven importer)
- MongoDB running locally on port 27017
- Lombok plugin/annotation processing enabled in IntelliJ if the IDE does not recognize Lombok-generated methods

## Run
1. Open the folder containing `pom.xml` in IntelliJ.
2. Set Project SDK and language level to 17.
3. Reload Maven.
4. Start MongoDB.
5. Run `MoviebookingApplication`.

Database: `moviebooking`; collections are created automatically when data is saved.

## Sample requests

### Add a movie
`POST http://localhost:8080/api/movies`
```json
{"id":"M101","name":"Leo","genre":"Action","price":200}
```

### Add a booking
`POST http://localhost:8080/api/bookings`
```json
{"id":"B101","movieId":"M101","customerName":"Sanjay","seats":2}
```

### Add another movie with no booking
`POST http://localhost:8080/api/movies`
```json
{"id":"M102","name":"Beast","genre":"Action","price":180}
```

### Useful endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/movies` | Add movie |
| GET | `/api/movies` | All movies |
| GET | `/api/movies/{id}` | One movie |
| PUT | `/api/movies/{id}` | Update movie |
| DELETE | `/api/movies/{id}` | Delete movie and its bookings |
| POST | `/api/bookings` | Add booking; verifies movie exists |
| GET | `/api/bookings` | All bookings |
| GET | `/api/bookings/{id}` | One booking |
| PUT | `/api/bookings/{id}` | Update booking |
| DELETE | `/api/bookings/{id}` | Delete booking |
| GET | `/api/movies/{movieId}/bookings` | Bookings for one movie |
| GET | `/api/movies/with-bookings` | Only movies with at least one booking |
| GET | `/api/movies/without-bookings` | Movies with no bookings |
| GET | `/api/stats/movie-count` | Count movies |
| GET | `/api/stats/booking-count` | Count bookings |
| GET | `/api/stats/movies/{movieId}/booking-count` | Count bookings for one movie |
| GET | `/api/stats/seats` | Sum seats across bookings |
| GET | `/api/stats/movies/{movieId}/seats` | Sum seats for one movie |
| GET | `/api/stats/price/average` | Average movie price |
| GET | `/api/stats/price/min` | Minimum movie price |
| GET | `/api/stats/price/max` | Maximum movie price |
| GET | `/api/stats/booking-count-by-movie` | Group/count bookings by movie ID |
| GET | `/api/stats/seats-by-movie` | Group/sum seats by movie ID |
| GET | `/api/movies/sort-by-price?desc=true` | Sort by price |
| GET | `/api/movies/by-genre/Action` | Filter by genre |
| GET | `/api/movies/search?name=leo` | Search movie name |
| GET | `/api/bookings/customer/Sanjay` | Find bookings by customer |

## Notes
- `Movie.id` and `Booking.movieId` are strings in this project. Use matching string IDs when testing.
- IDs are explicitly supplied in the sample requests. If you omit an ID, MongoDB will generate an ObjectId, which Spring maps to a string; for lab practice, explicit IDs make the relationship easy to follow.
- No automatic cascade behavior exists between MongoDB collections. Deleting a movie explicitly deletes its bookings first.
- These Java-side aggregate-like methods load documents into the application and calculate counts, sums, averages, minimums, maximums, and groups in memory. They are easy to learn for lab exercises but can be inefficient for large datasets; database-side aggregation is usually better at scale.
- The delete operation performs two writes and is not transactional by default. For real applications, handle partial failures or use a supported transaction setup.
