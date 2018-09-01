package com.github.filipmalczak.vent.api.general.query;



import com.github.filipmalczak.vent.api.temporal.TemporallyEnabled;

import java.time.LocalDateTime;
import java.util.function.Supplier;


public interface VentQuery<FindResult, CountResult, ExistsResult> extends TemporallyEnabled {
    FindResult find(Supplier<LocalDateTime> queryAt);

    default FindResult find(){
        return find(getTemporalService());
    }

    default FindResult find(LocalDateTime queryAt){
        return find(() -> queryAt);
    }

    CountResult count(Supplier<LocalDateTime> queryAt);

    default CountResult count(){
        return count(getTemporalService());
    }

    default CountResult count(LocalDateTime queryAt){
        return count(() -> queryAt);
    }

    ExistsResult exists(Supplier<LocalDateTime> queryAt);

    default ExistsResult exists(){
        return exists(getTemporalService());
    }

    default ExistsResult exists(LocalDateTime queryAt){
        return exists(() -> queryAt);
    }
}
