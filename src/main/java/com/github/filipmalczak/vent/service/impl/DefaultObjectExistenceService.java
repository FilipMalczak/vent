package com.github.filipmalczak.vent.service.impl;

import com.github.filipmalczak.vent.dto.Operation;
import com.github.filipmalczak.vent.model.Vent;
import com.github.filipmalczak.vent.model.VentObject;
import com.github.filipmalczak.vent.repository.Objects;
import com.github.filipmalczak.vent.service.CompactingService;
import com.github.filipmalczak.vent.service.ObjectExistenceService;
import com.github.filipmalczak.vent.service.TimestampService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class DefaultObjectExistenceService implements ObjectExistenceService {
    @Autowired
    private Objects objects;

    @Autowired
    private TimestampService timestampService;

    @Autowired
    private CompactingService compactingService;

    @Override
    public Mono<ObjectId> create(Mono<ObjectId> objectId, Mono<Map> initialValue) {
        return initialValue.map(initVal ->
            new Vent(Operation.CREATE, initVal, timestampService.now())
        ).flatMap(
            vent -> objectId.flatMap( id ->
                    objects.save(
                        VentObject.builder().
                            objectId(id).
                            event(vent).
                            lastCompacted(vent.getTimestamp()).
                            build()
                    )
            )
        ).map( o ->
            o.getObjectId()
        );
    }

    @Override
    public Mono<Void> delete(Mono<ObjectId> objectId) {
        return objects.deleteById(objectId).then(compactingService.compact(objectId));
    }

    @Override
    public Mono<VentObject> find(Mono<ObjectId> objectId) {
        return objects.findById(objectId);
    }
}
