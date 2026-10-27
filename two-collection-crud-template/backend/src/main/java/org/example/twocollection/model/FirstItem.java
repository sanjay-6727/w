package org.example.twocollection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "firstItems")
public class FirstItem {
    @Id
    private String id;

    private String name;
    private String category;
    private double price;
}