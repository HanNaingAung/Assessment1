package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@ToString
@Document(collection = "promotions")
public class Promotion {
    @Id
    private Long id;
    @NotNull(message = "Item Id cannot be empty.")
    private Long itemId;
    private String description;
    @NotNull(message = "Start Date cannot be empty.")
    private Date startDate;
    @NotNull(message = "End Date cannot be empty.")
    private Date endDate;
    @NotBlank(message = "Promote From cannot be empty.")
    private String promoteFrom;
    private Date createdAt;
    private Date updatedAt;

}