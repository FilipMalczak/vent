package com.github.filipmalczak.vent.testing;

import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.filipmalczak.vent.helper.Struct.list;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
public class Times {
    private LocalDateTime startTime;
    private Duration interval;

    public List<LocalDateTime> justNow(){
        return just(LocalDateTime.now());
    }

    public List<LocalDateTime> just(LocalDateTime localDateTime){
        return list(localDateTime);
    }

    public List<LocalDateTime> byInterval(int size){
        return IntStream.range(0, size).
            mapToObj(this::after).
            collect(toList());
    }

    public LocalDateTime after(int intervals){
        return startTime.plus(interval.multipliedBy(intervals));
    }

    public static Times defaultFromMilleniumBreak(){
        return new Times(LocalDateTime.of(2000, 1, 1, 12, 0), Duration.ofMinutes(5));
    }
}
