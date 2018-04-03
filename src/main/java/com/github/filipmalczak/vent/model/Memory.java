package com.github.filipmalczak.vent.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "history")
public class Memory {
    @Id
    private ObjectId _id;
    private ObjectId ventObjectId;
    private List<VentInterval> intervals;
}
