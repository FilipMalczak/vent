package com.github.filipmalczak.vent.embedded.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public interface TemporalService {
    LocalDateTime now();

    default ZoneId getTimezone(){
        return ZoneId.systemDefault();
    }

    default Date dateNow(){
        return toDate(now());
    }

    default Date toDate(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(getTimezone()).toInstant());
    }

    default LocalDateTime fromDate(Date date){
        return LocalDateTime.ofInstant(date.toInstant(), getTimezone());
    }

    default long timestampNow(){
        return toTimestamp(now());
    }

    default long toTimestamp(LocalDateTime localDateTime){
        return toDate(localDateTime).getTime();
    }

    default LocalDateTime fromTimestamp(long timestamp){
        return fromDate(new Date(timestamp));
    }
}
