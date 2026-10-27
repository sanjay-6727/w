package org.example.moviebooking.Repository;

import org.example.moviebooking.Model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByMovieId(String movieId);
}
