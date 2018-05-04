package com.github.filipmalczak.vent.web.integration;

import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.web.model.ObjectView;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
public class SnapshotMapping extends ConfigurableMapper {
    @Override
    protected void configure(MapperFactory factory) {
        factory.classMap(ObjectSnapshot.class, ObjectView.class);
    }
}
