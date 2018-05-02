package com.github.filipmalczak.vent.embedded.model.events;

import com.github.filipmalczak.vent.velvet.Velvet;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class DeleteValue implements Event {
    private String path;
    @Getter
    private LocalDateTime occuredOn;

    @Override
    public Mono<Map> apply(Mono<Map> mapMono) {
        return mapMono.map(m -> {
            Velvet.parse(path).bind(m).delete();
            return m;
        });
    }
}
