package com.github.filipmalczak.vent.web.client.temporal;


import com.github.filipmalczak.vent.api.temporal.AbstractRemoteTemporalService;
import com.github.filipmalczak.vent.api.temporal.RemoteTemporalService;
import com.github.filipmalczak.vent.web.client.utils.GetHelper;
import lombok.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.github.filipmalczak.vent.web.paths.CommonPaths.TEMPORAL_NOW;
import static com.github.filipmalczak.vent.web.paths.CommonPaths.TEMPORAL_TIMEZONE;

//todo: remote temporal service that will sync the difference between remote and local time and perform transmission only on demand, while yielding now() by using LocalDateTime.now().plus(localToRemoteDifference)
@EqualsAndHashCode
@ToString
public class NaiveWebTemporalService extends AbstractRemoteTemporalService {
    private GetHelper getHelper;

    public NaiveWebTemporalService(WebClient webClient) {
        this.getHelper = GetHelper.over(webClient);
    }

    @Override
    public LocalDateTime now() {
        return getHelper.getAs(TEMPORAL_NOW, LocalDateTime.class);
    }

    @Override
    public ZoneId getTimezone() {
        return getHelper.getAs(TEMPORAL_TIMEZONE, ZoneId.class);
    }
}
