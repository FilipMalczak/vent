package com.github.filipmalczak.vent.embedded.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.LinkedList;
import java.util.List;

@Getter
@EqualsAndHashCode
@ToString
public class VentDbDescriptor {
    @Id
    private ObjectId id;
    private List<String> managedCollections = new LinkedList<>();

    /**
     * @return true if collection was not managed yet, else false; "does the descriptor need to be persisted after this
     * operation?"
     */
    public boolean manage(String collection){
        if (managedCollections.contains(collection))
            return false;
        managedCollections.add(collection);
        return true;
    }
}
