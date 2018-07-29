package com.github.filipmalczak.vent.api.temporal;

import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class AbstractRemoteTemporalService implements RemoteTemporalService {
    @Override
    public TemporalService accountingForTransmissionTime() {
        return new AccountingForTransmissionTimeTemporalService(this);
    }


    @AllArgsConstructor
    protected class AccountingForTransmissionTimeTemporalService implements TemporalService {
        private TemporalService transmissionTimeAgnosticImpl;

        @Override
        public LocalDateTime now() {
            LocalDateTime beforeTransmission = LocalDateTime.now();
            LocalDateTime remote = transmissionTimeAgnosticImpl.now();
            LocalDateTime afterTransmission = LocalDateTime.now();
            return remote.plus(Duration.between(beforeTransmission, afterTransmission));
        }

        @Override
        public ZoneId getTimezone() {
            return transmissionTimeAgnosticImpl.getTimezone();
        }
    }
}
