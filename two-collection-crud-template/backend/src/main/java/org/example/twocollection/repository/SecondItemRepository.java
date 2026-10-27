package org.example.twocollection.repository;

import org.example.twocollection.model.SecondItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface SecondItemRepository extends MongoRepository<SecondItem, String> {
    List<SecondItem> findByFirstItemId(String firstItemId);
    List<SecondItem> findByPersonNameContainingIgnoreCase(String personName);
    long countByFirstItemId(String firstItemId);
}