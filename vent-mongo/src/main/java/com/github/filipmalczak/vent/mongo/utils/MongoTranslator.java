package com.github.filipmalczak.vent.mongo.utils;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import org.bson.types.ObjectId;

public class MongoTranslator {
    private MongoTranslator() {}

    public static VentId fromMongo(ObjectId objectId){
        return new VentId(objectId.toHexString());
    }

    public static ObjectId toMongo(VentId ventId){
        return new ObjectId(ventId.getValue());
    }

    public static Success fromMongo(com.mongodb.reactivestreams.client.Success success){
        return Success.SUCCESS;
    }

    public static com.mongodb.reactivestreams.client.Success toMongo(Success success){
        return com.mongodb.reactivestreams.client.Success.SUCCESS;
    }
}
