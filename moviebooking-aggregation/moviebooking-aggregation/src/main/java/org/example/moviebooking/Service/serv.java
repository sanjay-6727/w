package org.example.moviebooking.Service;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.example.moviebooking.Model.Booking;
import org.example.moviebooking.Model.Movie;
import org.example.moviebooking.Repository.BookingRepository;
import org.example.moviebooking.Repository.MovieRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class serv {
    private final MovieRepository movieRepository;
    private final BookingRepository bookingRepository;
    private final MongoTemplate mongoTemplate;

    public List<Movie> getMovies() { return movieRepository.findAll(); }
    public Movie addMovie(Movie movie) { return movieRepository.save(movie); }
    public Movie updateMovie(String id, Movie movie) { movie.setId(id); return movieRepository.save(movie); }
    public void deleteMovie(String id) { movieRepository.deleteById(id); }
    public List<Movie> getMoviesByGenre(String genre) { return movieRepository.findByGenreIgnoreCase(genre); }
    public List<Movie> searchMovies(String name) { return movieRepository.findByNameContainingIgnoreCase(name); }

    public List<Booking> getBookings() { return bookingRepository.findAll(); }
    public Booking addBooking(Booking booking) { return bookingRepository.save(booking); }
    public void deleteBooking(String id) { bookingRepository.deleteById(id); }
    public List<Booking> getBookingsForMovie(String movieId) { return bookingRepository.findByMovieId(movieId); }

    // $lookup joins movies._id with bookings.movieId; $match excludes unbooked movies.
    public List<Document> getMoviesWithBookings() {
        AggregationOperation lookup = context -> new Document("$lookup",
            new Document("from", "bookings")
                .append("localField", "_id")
                .append("foreignField", "movieId")
                .append("as", "bookingDetails"));
        AggregationOperation onlyBooked = context -> new Document("$match",
            new Document("bookingDetails.0", new Document("$exists", true)));
        AggregationOperation addStats = context -> new Document("$addFields",
            new Document("bookingCount", new Document("$size", "$bookingDetails"))
                .append("totalTickets", new Document("$sum", "$bookingDetails.tickets")));
        AggregationOperation project = context -> new Document("$project",
            new Document("_id", 1).append("name", 1).append("genre", 1)
                .append("price", 1).append("duration", 1)
                .append("bookingCount", 1).append("totalTickets", 1));
        Aggregation aggregation = Aggregation.newAggregation(lookup, onlyBooked, addStats, project);
        return mongoTemplate.aggregate(aggregation, "movies", Document.class).getMappedResults();
    }

    // $lookup + $unwind + $group calculates booking count, ticket count, and revenue per movie.
    public List<Document> getBookingSummary() {
        AggregationOperation lookup = context -> new Document("$lookup",
            new Document("from", "bookings").append("localField", "_id")
                .append("foreignField", "movieId").append("as", "bookingDetails"));
        AggregationOperation unwind = context -> new Document("$unwind", "$bookingDetails");
        AggregationOperation group = context -> new Document("$group",
            new Document("_id", new Document("movieId", "$_id")
                .append("movieName", "$name").append("genre", "$genre"))
                .append("bookingCount", new Document("$sum", 1))
                .append("totalTickets", new Document("$sum", "$bookingDetails.tickets"))
                .append("revenue", new Document("$sum", "$bookingDetails.totalAmount")));
        AggregationOperation project = context -> new Document("$project",
            new Document("_id", 0).append("movieId", "$_id.movieId")
                .append("movieName", "$_id.movieName").append("genre", "$_id.genre")
                .append("bookingCount", 1).append("totalTickets", 1).append("revenue", 1));
        Aggregation aggregation = Aggregation.newAggregation(lookup, unwind, group, project);
        return mongoTemplate.aggregate(aggregation, "movies", Document.class).getMappedResults();
    }
}
