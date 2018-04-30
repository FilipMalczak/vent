package com.github.filipmalczak.vent.api;

import lombok.Value;
import org.bson.types.ObjectId;

@Value
public class VentId {
    private String value;

    public ObjectId toMongoId(){
        return new ObjectId(value);
    }

    public static VentId fromMongoId(ObjectId mongoId){
        return new VentId(mongoId.toHexString());
    }
}
