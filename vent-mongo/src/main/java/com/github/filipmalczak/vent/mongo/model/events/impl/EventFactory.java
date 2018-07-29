package com.github.filipmalczak.vent.mongo.model.events.impl;

import lombok.AllArgsConstructor;

import java.util.Map;

//occuredOn should always be null - caller will take care of assigning timestamp
@AllArgsConstructor
public class EventFactory {

    public Create create(Map initialState){
        return new Create(initialState, null);
    }

    public Delete delete(){
        return new Delete(null);
    }

    public Update update(Map newValue){
        return new Update(newValue, null);
    }

    public PutValue putValue(String path, Object value){
        return new PutValue(path, value, null);
    }

    public DeleteValue deleteValue(String path){
        return new DeleteValue(path, null);
    }

    //todo: merge: update, but does not remove non-overriding fields in argument; doable with batch PUT too; which is better?
    //todo delete: should "close" a page, for fast access of the past and faster access to "not found anymore"
    //todo: transaction: something like batch, but with links to same kind of event in other objects (possibly cross-collection)
}
