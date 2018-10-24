package com.github.filipmalczak.vent.mongo.extension.optimization;

import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.mongo.extension.scan.model.IdentifiablePage;
import reactor.core.publisher.Mono;

public interface Optimizer {
    Mono<Success> optimize(IdentifiablePage page);

    // todo: it would be useful to be able to configure whether single optimization should result in one or many new pages
    // e.g. we were running without any optimization for a long time and we have pages that keep 1000+ events
    // we could split them to few pages, say 100 events per page, instead of leaving an old one with 1000+ events and
    // creating a new one
}
