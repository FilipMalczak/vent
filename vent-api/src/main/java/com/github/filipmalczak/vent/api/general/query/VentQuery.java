package com.github.filipmalczak.vent.api.general.query;



import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;

import java.time.LocalDateTime;


public interface VentQuery<FindResult, CountResult, ExistsResult> extends TemporallyEnabled {
    FindResult find(LocalDateTime queryAt);
    CountResult count(LocalDateTime queryAt);
    ExistsResult exists(LocalDateTime queryAt);
}
