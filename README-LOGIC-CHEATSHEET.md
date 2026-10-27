COMPOSITE KEY: 
@Document(collection = "enrollments")
@CompoundIndex(
    name = "student_course_unique",
    def = "{'studentId': 1, 'courseId': 1}",
    unique = true
)
public class Enrollment {

    @Id
    private String id;

    private String studentId;
    private String courseId;
    private String grade;
}

Extend MongoRepository and declare only the additional query methods you need.
1. MovieRepository.java
Create this file inside org.example.moviebooking.Repository.
package org.example.moviebooking.Repository;

import org.example.moviebooking.Model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {

    // Find movies by genre
    List<Movie> findByGenreIgnoreCase(String genre);

    // Find movies whose price is greater than or equal to a value
    List<Movie> findByPriceGreaterThanEqual(double price);

    // Search movie names without case sensitivity
    List<Movie> findByNameContainingIgnoreCase(String name);
}



Which methods do you get automatically?
Because you extend MongoRepository<Movie, String>, Spring provides these methods automatically:
Method	Purpose
save(movie)	Insert or update a movie
findAll()	Get all movies
findById(id)	Find a movie by ID
deleteById(id)	Delete a movie by ID
deleteAll()	Delete all movies
count()	Count all movies
existsById(id)	Check whether a movie exists
findAllById(ids)	Find movies matching a collection of IDs


You don't have to write the implementation of these methods.
2. BookingRepository.java
Create this file inside the same Repository package.
package org.example.moviebooking.Repository;

import org.example.moviebooking.Model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    // Find all bookings for a particular movie
    List<Booking> findByMovieId(String movieId);

    // Count bookings for a particular movie
    long countByMovieId(String movieId);

    // Find bookings by customer name, ignoring case
    List<Booking> findByCustomerNameIgnoreCase(String customerName);
}



Which methods do you get automatically?
Because you extend MongoRepository<Booking, String>, Spring provides:
Method	Purpose
save(booking)	Insert or update a booking
findAll()	Get all bookings
findById(id)	Find a booking by ID
deleteById(id)	Delete a booking
deleteAll()	Delete all bookings
count()	Count all bookings
existsById(id)	Check whether a booking exists
findAllById(ids)	Find bookings matching a collection of IDs


3. How these repositories connect to your service
Your service already contains the following calls. Here is where they come from:
Service method call	Defined where?
movieRepository.save(movie)	Automatically provided by MongoRepository
movieRepository.findAll()	Automatically provided
movieRepository.findById(id)	Automatically provided
movieRepository.deleteById(id)	Automatically provided
movieRepository.count()	Automatically provided
movieRepository.findAllById(ids)	Automatically provided
movieRepository.findByGenreIgnoreCase(genre)	You declare it in MovieRepository
movieRepository.findByPriceGreaterThanEqual(price)	You declare it in MovieRepository
movieRepository.findByNameContainingIgnoreCase(name)	You declare it in MovieRepository
bookingRepository.save(booking)	Automatically provided
bookingRepository.findAll()	Automatically provided
bookingRepository.findById(id)	Automatically provided
bookingRepository.deleteById(id)	Automatically provided
bookingRepository.deleteAll(...)	Automatically provided
bookingRepository.count()	Automatically provided
bookingRepository.findByMovieId(movieId)	You declare it in BookingRepository
bookingRepository.countByMovieId(movieId)	You declare it in BookingRepository
bookingRepository.findByCustomerNameIgnoreCase(name)	You declare it in BookingRepository


Important: deleteAll(bookingRepository.findByMovieId(movieId)) uses the built-in deleteAll(Iterable<? extends T>) method, so you do not need to declare it yourself.
4. How does Spring understand custom method names?
This is called a derived query. Spring Data reads the method name and generates the database query.
Method name	Meaning
findByGenreIgnoreCase(genre)	Find movies by genre, ignoring letter case
findByPriceGreaterThanEqual(price)	Find movies where price ≥ given price
findByNameContainingIgnoreCase(name)	Find movies whose names contain the search text
findByMovieId(movieId)	Find bookings with the specified movie ID
countByMovieId(movieId)	Count bookings with the specified movie ID
findByCustomerNameIgnoreCase(name)	Find bookings by customer name, ignoring case


For example:
movieRepository.findByGenreIgnoreCase("action");

This can find movies whose genre is "Action", "ACTION", or "action".

You do not need to write a MongoDB query manually for these methods.
5. How do you work across two collections without aggregation?
Your service does the joining in Java.

For example, your method:
public List<Movie> getMoviesWithBookings() {
    Set<String> bookedMovieIds = bookingRepository.findAll().stream()
            .map(Booking::getMovieId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

    return movieRepository.findAllById(bookedMovieIds);
}

works like this:
1. Fetch all bookings from the booking collection.
2. Extract the movieId from each booking.
3. Store unique movie IDs in a Set.
4. Fetch movies matching those IDs.
5. Return only movies that have bookings.
This approach does not use MongoDB aggregation. Java combines the results of separate repository queries.

# MOVIE BOOKING — LOGIC CHEAT SHEET
## Spring Boot + MongoDB + Aggregation | Beginner / Lab Viva Notes

This README explains how to **think through the logic**, not just copy code.

---

# 1. Understand the request before coding

For every feature, ask:
1. **What data do I need?** → Model
2. **Where is it stored?** → MongoDB collection
3. **What database action is needed?** → Repository or MongoTemplate
4. **What should happen in what order?** → Service
5. **Which URL/method should call it?** → Controller

Normal flow: `Client/Postman → Controller → Service → Repository/MongoTemplate → MongoDB`.

# 2. What each layer does

## Model — shape of one MongoDB document

```java
@Document(collection = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id private String id;
    private String name;
    private String genre;
    private double price;
    private int duration;
}
```

- `@Document` chooses the collection.
- `@Id` maps Java `id` to MongoDB `_id`.
- Lombok `@Data` generates getters/setters and common methods.
- Write the fields first. Use a separate model for a different document shape.

## Repository — ordinary database operations

```java
public interface MovieRepository extends MongoRepository<Movie, String> {
    List<Movie> findByGenreIgnoreCase(String genre);
    List<Movie> findByNameContainingIgnoreCase(String name);
}
```

Spring generates common methods:
- `findAll()` — get all documents
- `findById(id)` — get one document
- `save(object)` — insert or update
- `deleteById(id)` — delete one document
- `count()` — count documents

Derived query method names:
- `findByGenreIgnoreCase(genre)` → match genre case-insensitively
- `findByNameContainingIgnoreCase(name)` → partial, case-insensitive search
- `findByMovieId(movieId)` → find bookings with that movie ID

Use the Java field name in the method name.

## Service — actual logic

The service decides what needs to happen: save a movie, update a record, validate a booking, search, or run an aggregation.

## Controller — HTTP endpoints

- `@RestController` returns data, usually JSON.
- `@RequestMapping("/api")` adds a shared URL prefix.
- `@GetMapping` reads; `@PostMapping` creates; `@PutMapping` updates; `@DeleteMapping` deletes.
- `@PathVariable` reads a URL path value.
- `@RequestParam` reads a query parameter.
- `@RequestBody` converts request JSON into a Java object.

**Golden rule:** Controller = HTTP; Service = business logic; Repository = ordinary database access.

# 3. CRUD logic

## CREATE — add a movie

Flow: controller receives JSON → calls service → service calls `save` → MongoDB stores and returns the saved movie.

```java
@PostMapping("/movies")
public Movie addMovie(@RequestBody Movie movie) {
    return service.addMovie(movie);
}
```

```java
public Movie addMovie(Movie movie) {
    return movieRepository.save(movie);
}
```

Example JSON:
```json
{"name":"Interstellar","genre":"Sci-Fi","price":250,"duration":169}
```

## READ — all movies

```java
public List<Movie> getMovies() {
    return movieRepository.findAll();
}
```

## READ ONE — by ID

```java
public Movie getMovieById(String id) {
    return movieRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Movie not found"));
}
```

`findById` returns an `Optional`, because the ID may not exist.

## UPDATE

```java
public Movie updateMovie(String id, Movie movie) {
    movie.setId(id);
    return movieRepository.save(movie);
}
```

The URL ID is the target record. In a stricter app, check that the movie exists and update only allowed fields.

## DELETE

```java
public void deleteMovie(String id) {
    movieRepository.deleteById(id);
}
```

## SEARCH

```java
public List<Movie> getMoviesByGenre(String genre) {
    return movieRepository.findByGenreIgnoreCase(genre);
}
```

Spring Data interprets the method name and builds the simple query.

# 4. Booking logic

A booking links a customer to a movie. Here `Booking.movieId` stores the movie ID.

```json
{"customerName":"Sanjay","movieId":"PASTE_MOVIE_ID_HERE","tickets":2,"totalAmount":500}
```

Relationship: `movies._id` ↔ `bookings.movieId`. Values must match exactly and have compatible types.

Basic save:
```java
public Booking addBooking(Booking booking) {
    return bookingRepository.save(booking);
}
```

A safer service validates the number of tickets and movie existence, then calculates the amount:
```java
public Booking createBooking(Booking booking) {
    if (booking.getTickets() <= 0) {
        throw new IllegalArgumentException("Tickets must be greater than zero");
    }
    Movie movie = movieRepository.findById(booking.getMovieId())
        .orElseThrow(() -> new RuntimeException("Movie not found"));
    booking.setTotalAmount(movie.getPrice() * booking.getTickets());
    return bookingRepository.save(booking);
}
```

# 5. Aggregation — how to think

Use aggregation when MongoDB must **join, filter, calculate or group** data across documents/collections.

- `movies`: name, genre, price, duration
- `bookings`: customerName, movieId, tickets, totalAmount
- Join key: movie `_id` equals booking `movieId`.

Pipeline mental model: `START COLLECTION → $lookup → $match/$unwind → $group/$addFields → $project → RESULT`.

## `$lookup` — join collections

```java
lookup("bookings", "_id", "movieId", "bookingDetails")
```

Read it as: for each movie, find booking documents whose `movieId` matches the movie `_id`; put matches into the `bookingDetails` array.

## `$match` — filter documents

```java
match(Criteria.where("bookingDetails.0").exists(true))
```

Keep movies whose `bookingDetails` array contains at least one item.

## `$unwind` — flatten an array

```java
unwind("bookingDetails")
```

A movie with two bookings becomes two pipeline documents, one per booking. Useful before counting/summing individual bookings.

## `$group` — calculate per group

- `.count().as("bookingCount")` — count booking records
- `.sum("bookingDetails.tickets").as("totalTickets")` — sum ticket quantities
- `.sum("bookingDetails.totalAmount").as("revenue")` — sum booking amounts

The group key determines what counts as one group. Group by movie ID to get one summary per movie.

## `$addFields` — add calculated fields

Adds fields while retaining other fields unless a later stage projects them away. For example, count elements in `bookingDetails` to calculate booking count.

## `$project` — choose output fields

```java
project("name", "genre", "price", "bookingCount", "totalTickets")
    .and("_id").as("id")
```

Return only required fields; renaming `_id` to `id` is optional.

## Endpoint: `/api/movies/with-bookings`

Logic: start from movies → `$lookup` bookings → `$match` movies with bookings → `$addFields` booking count/ticket total → `$project` response fields.

## Endpoint: `/api/movies/booking-summary`

Logic: start from movies → `$lookup` → `$unwind` each booking → `$group` per movie → count bookings and sum tickets/revenue → `$project` clean output.

Use `MongoTemplate` for custom aggregation pipelines. Use `MongoRepository` for ordinary CRUD.

# 6. Build an aggregation for a new question

| Wording in question | Likely stage |
|---|---|
| Join two collections | `$lookup` |
| Keep only matching results | `$match` |
| Process every array item separately | `$unwind` |
| Total/count per movie | `$group` |
| Add a calculated field | `$addFields` |
| Show only selected fields | `$project` |
| Sort highest first | `$sort` |
| Return first 5 | `$limit` |
| Skip first 10 | `$skip` |

Recipe: pick starting collection → identify join keys → lookup if needed → filter → unwind if calculations need individual joined items → group/calculate → project output → run with `mongoTemplate.aggregate(...)`.

# 7. Spring annotations — viva quick answers

- `@SpringBootApplication`: configures and starts the Spring Boot application.
- `@Document`: maps a class to a MongoDB collection.
- `@Id`: maps the document ID.
- `MongoRepository`: standard CRUD and derived queries.
- `@Service`: business logic layer.
- `@RestController`: handles HTTP requests and returns data.
- `@RequestMapping`: common URL prefix.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`: map HTTP methods.
- `@PathVariable`: value from URL path.
- `@RequestParam`: query-string value.
- `@RequestBody`: JSON request body → Java object.
- `@RequiredArgsConstructor`: Lombok creates a constructor for final fields.
- `MongoTemplate`: custom queries and aggregation pipelines.

# 8. Endpoints to remember

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/movies` | List movies |
| POST | `/api/movies` | Add movie |
| PUT | `/api/movies/{id}` | Update movie |
| DELETE | `/api/movies/{id}` | Delete movie |
| GET | `/api/movies/genre/{genre}` | Filter by genre |
| GET | `/api/movies/search?name=bat` | Partial name search |
| GET | `/api/bookings` | List bookings |
| POST | `/api/bookings` | Add booking |
| DELETE | `/api/bookings/{id}` | Delete booking |
| GET | `/api/movies/{movieId}/bookings` | Bookings for one movie |
| GET | `/api/movies/with-bookings` | Aggregation join and calculated fields |
| GET | `/api/movies/booking-summary` | Aggregation group/count/sums |

# 9. Common errors

1. **MongoDB connection refused:** start MongoDB; check URI and port.
2. **Wrong database:** check `spring.data.mongodb.uri` in `application.properties`.
3. **Empty aggregation result:** create bookings and ensure `Booking.movieId` matches movie `_id`.
4. **Join has no matches:** check key values and types. String `"123"` is not the same as an ObjectId.
5. **404 endpoint:** check controller mapping and port.
6. **400 request error:** check JSON property names and data types.
7. **Lombok error:** check dependency and IDE annotation-processing configuration.
8. **Wrong totals:** verify grouping key and `$unwind` behavior.
9. **Revenue missing/zero:** ensure `totalAmount` is numeric.
10. **Totals repeated:** avoid summing a movie-level field after unwind when it repeats for every booking.

# 10. Exam memory trick: M-R-S-C-A

- **M**odel = fields/document shape
- **R**epository = CRUD and simple searches
- **S**ervice = logic and order of operations
- **C**ontroller = URL and HTTP method
- **A**ggregation = join, filter, calculate, group

When you receive a new question, first explain the required behavior in plain English. Then decide which layer implements each step, and only then write the code.
