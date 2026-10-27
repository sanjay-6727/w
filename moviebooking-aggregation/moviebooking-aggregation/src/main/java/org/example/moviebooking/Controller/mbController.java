package org.example.moviebooking.Controller;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.example.moviebooking.Model.Booking;
import org.example.moviebooking.Model.Movie;
import org.example.moviebooking.Service.serv;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class mbController {
    private final serv service;

    @GetMapping("/movies") public List<Movie> getMovies() { return service.getMovies(); }
    @PostMapping("/movies") public Movie addMovie(@RequestBody Movie movie) { return service.addMovie(movie); }
    @PutMapping("/movies/{id}") public Movie updateMovie(@PathVariable String id, @RequestBody Movie movie) { return service.updateMovie(id, movie); }
    @DeleteMapping("/movies/{id}") public void deleteMovie(@PathVariable String id) { service.deleteMovie(id); }
    @GetMapping("/movies/genre/{genre}") public List<Movie> getByGenre(@PathVariable String genre) { return service.getMoviesByGenre(genre); }
    @GetMapping("/movies/search") public List<Movie> search(@RequestParam String name) { return service.searchMovies(name); }

    @GetMapping("/bookings") public List<Booking> getBookings() { return service.getBookings(); }
    @PostMapping("/bookings") public Booking addBooking(@RequestBody Booking booking) { return service.addBooking(booking); }
    @DeleteMapping("/bookings/{id}") public void deleteBooking(@PathVariable String id) { service.deleteBooking(id); }
    @GetMapping("/movies/{movieId}/bookings") public List<Booking> bookingsForMovie(@PathVariable String movieId) { return service.getBookingsForMovie(movieId); }

    @GetMapping("/movies/with-bookings") public List<Document> moviesWithBookings() { return service.getMoviesWithBookings(); }
    @GetMapping("/movies/booking-summary") public List<Document> bookingSummary() { return service.getBookingSummary(); }
}
