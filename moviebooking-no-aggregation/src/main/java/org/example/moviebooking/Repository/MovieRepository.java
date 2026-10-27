package org.example.moviebooking.Repository;

import org.example.moviebooking.Model.Movie;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, String> {
    List<Movie> findByGenreIgnoreCase(String genre);
    List<Movie> findByPriceGreaterThanEqual(double price);
    List<Movie> findByPriceLessThanEqual(double price);
    List<Movie> findByNameContainingIgnoreCase(String name);
}
