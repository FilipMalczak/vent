package com.github.filipmalczak.vent.embedded.service;

import com.github.filipmalczak.vent.embedded.model.CollectionDescriptor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReactiveCollectionsRepo extends ReactiveMongoRepository<CollectionDescriptor, ObjectId> {
}
