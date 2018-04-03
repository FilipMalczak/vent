package com.github.filipmalczak.vent.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Document(collection = "ventObjects")
public class VentObject {
    @Id
    private ObjectId objectId;
    private Map lastSnapshot;
    @Singular
    private List<Vent> events;
    private LocalDateTime lastCompacted;
}
