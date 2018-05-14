package com.github.filipmalczak.vent.api.reactive;

import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.general.VentDb;
import com.github.filipmalczak.vent.traits.Reactive;

import static com.github.filipmalczak.vent.traits.adapters.Adapters.adapt;


public interface ReactiveVentDb extends VentDb<ReactiveVentCollection>, Reactive<BlockingVentDb> {

    //todo: consider adding version with Duration
    default BlockingVentDb asBlocking(){
        return adapt(this);
    }
}
