package org.example.moviebooking.Service;

import lombok.RequiredArgsConstructor;
import org.example.moviebooking.Model.Booking;
import org.example.moviebooking.Model.Movie;
import org.example.moviebooking.Repository.BookingRepository;
import org.example.moviebooking.Repository.MovieRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class serv {
    private final MovieRepository movieRepository;
    private final BookingRepository bookingRepository;

    // ---------- MOVIE CRUD ----------
    public Movie addMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie getMovie(String id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Movie not found: " + id));
    }

    public Movie updateMovie(String id, Movie updated) {
        Movie movie = getMovie(id);
        movie.setName(updated.getName());
        movie.setGenre(updated.getGenre());
        movie.setPrice(updated.getPrice());
        return movieRepository.save(movie);
    }

    // ---------- BOOKING CRUD ----------
    public Booking addBooking(Booking booking) {
        getMovie(booking.getMovieId()); // ensure referenced movie exists
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBooking(String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Booking not found: " + id));
    }

    public Booking updateBooking(String id, Booking updated) {
        Booking booking = getBooking(id);
        getMovie(updated.getMovieId());
        booking.setMovieId(updated.getMovieId());
        booking.setCustomerName(updated.getCustomerName());
        booking.setSeats(updated.getSeats());
        return bookingRepository.save(booking);
    }

    public void deleteBooking(String id) {
        bookingRepository.deleteById(id);
    }

    public void deleteMovieAndBookings(String movieId) {
        getMovie(movieId);
        bookingRepository.deleteAll(bookingRepository.findByMovieId(movieId));
        movieRepository.deleteById(movieId);
    }

    public List<Booking> getBookingsForMovie(String movieId) {
        getMovie(movieId);
        return bookingRepository.findByMovieId(movieId);
    }

    // ---------- CROSS-COLLECTION OPERATIONS WITHOUT AGGREGATION ----------
    // Return only movies that have at least one booking.
    public List<Movie> getMoviesWithBookings() {
        Set<String> bookedMovieIds = bookingRepository.findAll().stream()
                .map(Booking::getMovieId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return movieRepository.findAllById(bookedMovieIds);
    }

    // Return movies that have no bookings.
    public List<Movie> getMoviesWithoutBookings() {
        Set<String> bookedMovieIds = bookingRepository.findAll().stream()
                .map(Booking::getMovieId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return movieRepository.findAll().stream()
                .filter(movie -> !bookedMovieIds.contains(movie.getId()))
                .toList();
    }

    // ---------- AGGREGATE-LIKE OPERATIONS USING JAVA, NOT MONGODB AGGREGATION ----------
    // Count all movies.
    public long countMovies() {
        return movieRepository.count();
    }

    // Count all bookings.
    public long countBookings() {
        return bookingRepository.count();
    }

    // Count bookings for one movie.
    public long countBookingsForMovie(String movieId) {
        getMovie(movieId);
        return bookingRepository.countByMovieId(movieId);
    }

    // Sum seats across all bookings.
    public long totalSeatsBooked() {
        return bookingRepository.findAll().stream()
                .mapToLong(Booking::getSeats)
                .sum();
    }

    // Sum seats for a movie.
    public long totalSeatsForMovie(String movieId) {
        return bookingRepository.findByMovieId(movieId).stream()
                .mapToLong(Booking::getSeats)
                .sum();
    }

    // Average movie price; OptionalDouble is empty if there are no movies.
    public OptionalDouble averageMoviePrice() {
        return movieRepository.findAll().stream()
                .mapToDouble(Movie::getPrice)
                .average();
    }

    public OptionalDouble minimumMoviePrice() {
        return movieRepository.findAll().stream()
                .mapToDouble(Movie::getPrice)
                .min();
    }

    public OptionalDouble maximumMoviePrice() {
        return movieRepository.findAll().stream()
                .mapToDouble(Movie::getPrice)
                .max();
    }

    // Group bookings by movieId and count them.
    public Map<String, Long> bookingCountByMovie() {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getMovieId() != null)
                .collect(Collectors.groupingBy(Booking::getMovieId, Collectors.counting()));
    }

    // Group seats by movieId and sum them.
    public Map<String, Integer> seatTotalsByMovie() {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getMovieId() != null)
                .collect(Collectors.groupingBy(
                        Booking::getMovieId,
                        Collectors.summingInt(Booking::getSeats)
                ));
    }

    // Sort movies by price ascending / descending.
    public List<Movie> sortMoviesByPrice(boolean descending) {
        Comparator<Movie> comparator = Comparator.comparingDouble(Movie::getPrice);
        if (descending) {
            comparator = comparator.reversed();
        }
        return movieRepository.findAll().stream().sorted(comparator).toList();
    }

    // Filter helpers using repository methods.
    public List<Movie> findMoviesByGenre(String genre) {
        return movieRepository.findByGenreIgnoreCase(genre);
    }

    public List<Movie> findMoviesAtOrAbovePrice(double price) {
        return movieRepository.findByPriceGreaterThanEqual(price);
    }

    public List<Movie> searchMoviesByName(String name) {
        return movieRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Booking> findBookingsByCustomer(String customerName) {
        return bookingRepository.findByCustomerNameIgnoreCase(customerName);
    }
}
