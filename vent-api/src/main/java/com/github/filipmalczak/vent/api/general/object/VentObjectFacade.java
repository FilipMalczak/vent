package com.github.filipmalczak.vent.api.general.object;

public interface VentObjectFacade<Confirmation, Snapshot>
    extends
        VentObjectReadFacade<Snapshot>,
        VentObjectWriteFacade<Confirmation> {
}
