package com.github.filipmalczak.vent.testimpl;

import com.github.filipmalczak.vent.api.EventConfirmation;
import com.github.filipmalczak.vent.api.ObjectSnapshot;
import com.github.filipmalczak.vent.api.Success;
import com.github.filipmalczak.vent.api.VentId;
import com.github.filipmalczak.vent.api.blocking.BlockingVentCollection;
import com.github.filipmalczak.vent.api.blocking.BlockingVentDb;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.filipmalczak.vent.helper.Struct.list;

@AllArgsConstructor
@Slf4j
public class LoggingDbWrapper implements ReactiveVentDb {
    private StackTracer stackTracer;
    private boolean logArgs;
    private boolean logResults;
    private boolean logExceptions;
    private ReactiveVentDb delegate;

    @Override
    public BlockingVentDb asBlocking() {
        return new BlockingVentDb() {
            @Override
            public BlockingVentCollection getCollection(String collectionName) {
                return new BlockingVentCollection() {
                    private BlockingVentCollection delegateCollection = delegate.asBlocking().getCollection(collectionName);

                    @Override
                    @SneakyThrows
                    public Success drop() {
                        try {
                            logHierarchy();
                            logArgs();
                            Success result = delegateCollection.drop();
                            return logResult(result);
                        } catch (Throwable t){
                            throw logException(t);
                        }
                    }

                    @Override
                    @SneakyThrows
                    public VentId create(Map initialState) {
                        try {
                            logHierarchy();
                            logArgs(initialState);
                            VentId result = delegateCollection.create(initialState);
                            return logResult(result);
                        } catch (Throwable t){
                            throw logException(t);
                        }
                    }

                    @Override
                    @SneakyThrows
                    public EventConfirmation putValue(VentId id, String path, Object value) {
                        try {
                            logHierarchy();
                            logArgs(id, path, value);
                            EventConfirmation result = delegateCollection.putValue(id, path, value);
                            return logResult(result);
                        } catch (Throwable t){
                            throw logException(t);
                        }
                    }

                    @Override
                    @SneakyThrows
                    public EventConfirmation deleteValue(VentId id, String path) {
                        try {
                            logHierarchy();
                            logArgs(id, path);
                            EventConfirmation result = delegateCollection.deleteValue(id, path);
                            return logResult(result);
                        } catch (Throwable t){
                            throw logException(t);
                        }
                    }

                    @Override
                    @SneakyThrows
                    public ObjectSnapshot get(VentId id, LocalDateTime queryAt) {
                        try {
                            logHierarchy();
                            logArgs(id, queryAt);
                            ObjectSnapshot result = delegateCollection.get(id, queryAt);
                            return logResult(result);
                        } catch (Throwable t){
                            throw logException(t);
                        }
                    }

                    @Override
                    @SneakyThrows
                    public Stream<VentId> identifyAll(LocalDateTime queryAt) {
                        try {
                            logHierarchy();
                            logArgs(queryAt);
                            Stream<VentId> result = delegateCollection.identifyAll(queryAt);
                            return logResult(result);
                        } catch (Throwable t){
                            throw logException(t);
                        }
                    }

                    @Override
                    @SneakyThrows
                    public EventConfirmation update(VentId id, Map newState) {
                        try {
                            logHierarchy();
                            logArgs(id, newState);
                            EventConfirmation result = delegateCollection.update(id, newState);
                            return logResult(result);
                        } catch (Throwable t){
                            throw logException(t);
                        }
                    }
                };
            }
        };
    }

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        return new ReactiveVentCollection() {
            private ReactiveVentCollection delegateCollection = delegate.getCollection(collectionName);

            @Override
            @SneakyThrows
            public Mono<Success> drop() {
                try {
                    logHierarchy();
                    logArgs();
                    Mono<Success> result = delegateCollection.drop();
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }

            @Override
            @SneakyThrows
            public Mono<VentId> create(Map initialState) {
                try {
                    logHierarchy();
                    logArgs(initialState);
                    Mono<VentId> result = delegateCollection.create(initialState);
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }

            @Override
            @SneakyThrows
            public Mono<EventConfirmation> putValue(VentId id, String path, Object value) {
                try {
                    logHierarchy();
                    logArgs(id, path, value);
                    Mono<EventConfirmation> result = delegateCollection.putValue(id, path, value);
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }

            @Override
            @SneakyThrows
            public Mono<EventConfirmation> deleteValue(VentId id, String path) {
                try {
                    logHierarchy();
                    logArgs(id, path);
                    Mono<EventConfirmation> result = delegateCollection.deleteValue(id, path);
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }

            @Override
            @SneakyThrows
            public Mono<ObjectSnapshot> get(VentId id, LocalDateTime queryAt) {
                try {
                    logHierarchy();
                    logArgs(id, queryAt);
                    Mono<ObjectSnapshot> result = delegateCollection.get(id, queryAt);
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }

            @Override
            @SneakyThrows
            public Flux<VentId> identifyAll(LocalDateTime queryAt) {
                try {
                    logHierarchy();
                    logArgs(queryAt);
                    Flux<VentId> result = delegateCollection.identifyAll(queryAt);
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }

            @Override
            @SneakyThrows
            public Mono<EventConfirmation> update(VentId id, Map newState) {
                try {
                    logHierarchy();
                    logArgs(id, newState);
                    Mono<EventConfirmation> result = delegateCollection.update(id, newState);
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }
        };
    }

    public static LoggingDbWrapper defaultWrapper(StackTracer stackTracer, ReactiveVentDb delegate){
        return new LoggingDbWrapper(stackTracer, true, true, true, delegate);
    }

    private void logHierarchy(){
        if (stackTracer != null)
            log.info(stackTracer.getCurrentHierarchy(3));
    }

    private void logArgs(Object... args){
        if (logArgs)
            log.info("Args: "+list(args));
    }

    private <T> Stream<T> logResult(Stream<T> result){
        if (logResults) {
            if (result == null) {
                log.info("Result stream = null");
                return null;
            }
            long timestamp = new Date().getTime();
            log.info("Result stream will log with: " + timestamp);
            return result.peek(v -> log.info(""+timestamp+" peeked element: "+v));
        }
        return result;
    }

    private <T> Mono<T> logResult(Mono<T> result){
        if (logResults) {
            if (result == null) {
                log.info("Result mono = null");
                return null;
            }
            long timestamp = new Date().getTime();
            log.info("Result mono will log with: " + timestamp);
            return result.log(""+timestamp);
        }
        return result;
    }

    private <T> Flux<T> logResult(Flux<T> result){
        if (logResults) {
            if (result == null) {
                log.info("Result flux = null");
                return null;
            }
            long timestamp = new Date().getTime();
            log.info("Result flux will log with: " + timestamp);
            return result.log(""+timestamp);
        }
        return result;
    }

    private <T> T logResult(T result){
        if (logResults)
            log.info("Result: "+result);
        return result;
    }

    private Throwable logException(Throwable t){
        if (logExceptions)
            log.error(t.toString());
        return t;
    }
}
