package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@ToString
@Document(collection = "promotions")
public class Promotion {
    @Id
    private Long id;
    private Long itemId;
    private String description;
    private Date startDate;
    private Date endDate;
    private String promoteFrom;
    private Date createdAt;
    private Date updatedAt;

}