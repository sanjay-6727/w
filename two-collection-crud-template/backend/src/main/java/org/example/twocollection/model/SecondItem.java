package org.example.twocollection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "secondItems")
public class SecondItem {
    @Id
    private String id;

    // This connects this document to a FirstItem document.
    private String firstItemId;

    private String personName;
    private int value;
}