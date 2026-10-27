package org.example.moviebooking.Controller;

import lombok.RequiredArgsConstructor;
import org.example.moviebooking.Model.Booking;
import org.example.moviebooking.Model.Movie;
import org.example.moviebooking.Service.serv;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.OptionalDouble;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class mbController {
    private final serv service;

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(NoSuchElementException ex) {
        return Map.of("error", ex.getMessage());
    }

    // Movie CRUD
    @PostMapping("/movies")
    public Movie addMovie(@RequestBody Movie movie) {
        return service.addMovie(movie);
    }

    @GetMapping("/movies")
    public List<Movie> getAllMovies() {
        return service.getAllMovies();
    }

    @GetMapping("/movies/{id}")
    public Movie getMovie(@PathVariable String id) {
        return service.getMovie(id);
    }

    @PutMapping("/movies/{id}")
    public Movie updateMovie(@PathVariable String id, @RequestBody Movie movie) {
        return service.updateMovie(id, movie);
    }

    @DeleteMapping("/movies/{id}")
    public Map<String, String> deleteMovie(@PathVariable String id) {
        service.deleteMovieAndBookings(id);
        return Map.of("message", "Movie and related bookings deleted");
    }

    // Booking CRUD
    @PostMapping("/bookings")
    public Booking addBooking(@RequestBody Booking booking) {
        return service.addBooking(booking);
    }

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return service.getAllBookings();
    }

    @GetMapping("/bookings/{id}")
    public Booking getBooking(@PathVariable String id) {
        return service.getBooking(id);
    }

    @PutMapping("/bookings/{id}")
    public Booking updateBooking(@PathVariable String id, @RequestBody Booking booking) {
        return service.updateBooking(id, booking);
    }

    @DeleteMapping("/bookings/{id}")
    public Map<String, String> deleteBooking(@PathVariable String id) {
        service.deleteBooking(id);
        return Map.of("message", "Booking deleted");
    }

    @GetMapping("/movies/{movieId}/bookings")
    public List<Booking> getBookingsForMovie(@PathVariable String movieId) {
        return service.getBookingsForMovie(movieId);
    }

    // Cross-collection operations
    @GetMapping("/movies/with-bookings")
    public List<Movie> getMoviesWithBookings() {
        return service.getMoviesWithBookings();
    }

    @GetMapping("/movies/without-bookings")
    public List<Movie> getMoviesWithoutBookings() {
        return service.getMoviesWithoutBookings();
    }

    // Count, sum, average, min, max, grouping, sorting
    @GetMapping("/stats/movie-count")
    public Map<String, Long> countMovies() {
        return Map.of("movieCount", service.countMovies());
    }

    @GetMapping("/stats/booking-count")
    public Map<String, Long> countBookings() {
        return Map.of("bookingCount", service.countBookings());
    }

    @GetMapping("/stats/movies/{movieId}/booking-count")
    public Map<String, Long> countBookingsForMovie(@PathVariable String movieId) {
        return Map.of("bookingCount", service.countBookingsForMovie(movieId));
    }

    @GetMapping("/stats/seats")
    public Map<String, Long> totalSeats() {
        return Map.of("totalSeats", service.totalSeatsBooked());
    }

    @GetMapping("/stats/movies/{movieId}/seats")
    public Map<String, Long> totalSeatsForMovie(@PathVariable String movieId) {
        return Map.of("totalSeats", service.totalSeatsForMovie(movieId));
    }

    @GetMapping("/stats/price/average")
    public Map<String, Object> averagePrice() {
        OptionalDouble value = service.averageMoviePrice();
        return Map.of("averagePrice", value.isPresent() ? value.getAsDouble() : 0.0);
    }

    @GetMapping("/stats/price/min")
    public Map<String, Object> minPrice() {
        OptionalDouble value = service.minimumMoviePrice();
        return Map.of("minPrice", value.isPresent() ? value.getAsDouble() : 0.0);
    }

    @GetMapping("/stats/price/max")
    public Map<String, Object> maxPrice() {
        OptionalDouble value = service.maximumMoviePrice();
        return Map.of("maxPrice", value.isPresent() ? value.getAsDouble() : 0.0);
    }

    @GetMapping("/stats/booking-count-by-movie")
    public Map<String, Long> bookingCountByMovie() {
        return service.bookingCountByMovie();
    }

    @GetMapping("/stats/seats-by-movie")
    public Map<String, Integer> seatTotalsByMovie() {
        return service.seatTotalsByMovie();
    }

    @GetMapping("/movies/sort-by-price")
    public List<Movie> sortMovies(@RequestParam(defaultValue = "false") boolean desc) {
        return service.sortMoviesByPrice(desc);
    }

    @GetMapping("/movies/by-genre/{genre}")
    public List<Movie> moviesByGenre(@PathVariable String genre) {
        return service.findMoviesByGenre(genre);
    }

    @GetMapping("/movies/search")
    public List<Movie> searchMovies(@RequestParam String name) {
        return service.searchMoviesByName(name);
    }

    @GetMapping("/bookings/customer/{name}")
    public List<Booking> bookingsByCustomer(@PathVariable String name) {
        return service.findBookingsByCustomer(name);
    }
}
