package com.github.filipmalczak.vent.api.temporal;



import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Supplier;

/**
 * General abstraction of "what time is it now?" service. Usually, calling now() should yield time of
 * calling that method, and not that method returning.
 *
 * E.g. implementation of now() takes 5 seconds to finish, because it uses some NTP server and connection is bad;
 * when called at 12:00:00, it will return at 12:00:05 and should return datetime representing 12:00:00.
 */
public interface TemporalService extends Supplier<LocalDateTime> {
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

    default LocalDateTime get(){
        return now();
    }
}
