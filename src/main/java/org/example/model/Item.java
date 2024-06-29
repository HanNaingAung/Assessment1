package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "items")
public class Item {

    @Id
    private Long id;
    private String name;
    private Integer duration;
    private Double distance;
    private Double rating;
    private Double deliveryFee;
    private List<String> categories;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;

}
