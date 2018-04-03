package com.github.filipmalczak.vent.repository;

import com.github.filipmalczak.vent.model.Memory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface History extends ReactiveMongoRepository<Memory, ObjectId> {
}
