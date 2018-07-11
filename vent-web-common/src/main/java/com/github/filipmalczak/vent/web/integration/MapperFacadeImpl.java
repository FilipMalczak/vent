package com.github.filipmalczak.vent.web.integration;

import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.web.model.IdView;
import com.github.filipmalczak.vent.web.model.ObjectView;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

public class MapperFacadeImpl extends ConfigurableMapper {
    @Override
    protected void configure(MapperFactory factory) {
        super.configure(factory);
        factory.
            classMap(VentId.class, IdView.class).
            field("value", "id").
            register();
        factory.
            classMap(ObjectSnapshot.class, ObjectView.class).
            register();
    }
}
