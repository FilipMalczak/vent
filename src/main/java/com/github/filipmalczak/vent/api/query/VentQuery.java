package com.github.filipmalczak.vent.api.query;

import java.time.LocalDateTime;

public interface VentQuery<FindResult, CountResult, ExistsResult> {
    FindResult find(LocalDateTime queryAt);
    CountResult count(LocalDateTime queryAt);
    ExistsResult exists(LocalDateTime queryAt);
}
