package com.github.filipmalczak.vent.embedded.model.events.impl;

import com.github.filipmalczak.vent.embedded.service.TemporalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventFactory {
    @Autowired
    private TemporalService temporalService;

    public Create create(Map initialState){
        return new Create(initialState, temporalService.now());
    }

    public Delete delete(){
        return new Delete(temporalService.now());
    }

    public Update update(Map newValue){
        return new Update(newValue, temporalService.now());
    }

    public PutValue putValue(String path, Object value){
        return new PutValue(path, value, temporalService.now());
    }

    public DeleteValue deleteValue(String path){
        return new DeleteValue(path, temporalService.now());
    }

    //todo: merge: update, but does not remove non-overriding fields in argument; doable with batch PUT too; which is better?
    //todo delete: should "close" a page, for fast access of the past and faster access to "not found anymore"
    //todo: transaction: something like batch, but with links to same kind of event in other objects (possibly cross-collection)
}
