package com.github.filipmalczak.vent.repository;

import com.github.filipmalczak.vent.model.VentObject;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface Objects extends ReactiveMongoRepository<VentObject, ObjectId>{
}
