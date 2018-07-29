package com.github.filipmalczak.vent.api.temporal;

/**
 * Temporal service that is aware of the fact, that it is using some remote time source (e.g. NTP server).
 * As such, it is able to provide a temporal service that will account for how long transmission took.
 */
public interface RemoteTemporalService extends TemporalService {
    /**
     * Turns this service into one that is aware how long transmission took.
     *
     * E.g. implementation of now() calls a remote server and takes 5 seconds to finish, because connection is bad;
     * when called at 12:00:00 (server time), it will return at 12:00:05  (server time) and return datetime
     * representing 12:00:00.
     * As result of this method is aware of that transmission, it should take those 5 seconds into account and
     * return 12:00:05 instead.
     *
     * @return Transmission-time aware version of this instance
     */
    TemporalService accountingForTransmissionTime();
}
