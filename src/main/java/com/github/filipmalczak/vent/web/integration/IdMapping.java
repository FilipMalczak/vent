package com.github.filipmalczak.vent.web.integration;

import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.web.model.IdView;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

public class IdMapping extends ConfigurableMapper {
    @Override
    protected void configure(MapperFactory factory) {
        factory.
            classMap(VentId.class, IdView.class).
            field("value", "id").
            register();
    }
}
