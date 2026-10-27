package org.example.twocollection.repository;

import org.example.twocollection.model.FirstItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FirstItemRepository extends MongoRepository<FirstItem, String> {
    List<FirstItem> findByNameContainingIgnoreCase(String name);
    List<FirstItem> findByCategoryIgnoreCase(String category);
}