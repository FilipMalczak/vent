package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.service.PageService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class EmbeddedReactiveVentDb implements ReactiveVentDb {
    private @NonNull PageService pageService;

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        return new EmbeddedReactiveVentCollection(collectionName, pageService);
    }
}
