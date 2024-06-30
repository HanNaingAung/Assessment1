package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "items")
public class Item {

    @Id
    private Long id;
    @NotBlank(message = "Name cannot be empty.")
    private String name;
    @NotNull(message = "Duration cannot be empty.")
    private Integer duration;
    @NotNull(message = "Distance cannot be empty.")
    private Double distance;
    @NotNull(message = "Rating cannot be empty.")
    private Double rating;
    @NotNull(message = "Deliver Fee cannot be empty.")
    private Double deliveryFee;
    private List<String> categories;
    @NotBlank(message = "Name cannot be empty.")
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;

}
