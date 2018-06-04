package com.github.filipmalczak.vent.testing;

import com.github.filipmalczak.vent.api.model.EventConfirmation;
import com.github.filipmalczak.vent.api.model.ObjectSnapshot;
import com.github.filipmalczak.vent.api.model.Success;
import com.github.filipmalczak.vent.api.model.VentId;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentCollection;
import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveQueryBuilder;
import com.github.filipmalczak.vent.api.reactive.query.ReactiveVentQuery;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.filipmalczak.vent.helper.Struct.list;

//fixme while DB API grew, this was not updated; it should probably be deleted
//todo rethink, probably decorate with JDK proxy instead of this moloch of a flustercuck
@AllArgsConstructor
@Slf4j
public class LoggingDbWrapper implements ReactiveVentDb {
    private StackTracer stackTracer;
    private boolean logArgs;
    private boolean logResults;
    private boolean logExceptions;
    private ReactiveVentDb delegate;

    @Override
    public ReactiveVentCollection getCollection(String collectionName) {
        return new ReactiveVentCollection() {


            @Override
            public TemporalService getTemporalService() {
                return null;
            }

            private ReactiveVentCollection delegateCollection = delegate.getCollection(collectionName);

            @Override
            public String getName() {
                return delegateCollection.getName();
            }

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

            @Override
            public Mono<EventConfirmation> delete(VentId id) {
                return delegateCollection.delete(id);
            }

            @Override
            @SneakyThrows
            public ReactiveQueryBuilder<?, ? extends ReactiveVentQuery> queryBuilder() {
                try {
                    logHierarchy();
                    logArgs();
                    ReactiveQueryBuilder<?, ? extends ReactiveVentQuery> delegateBuilder = delegateCollection.queryBuilder();
                    ReactiveQueryBuilder<?, ? extends ReactiveVentQuery> result = (ReactiveQueryBuilder<?, ? extends ReactiveVentQuery>) Proxy.newProxyInstance(
                        delegateCollection.getClass().getClassLoader(),
                        new Class[]{ReactiveQueryBuilder.class},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                Object result = method.invoke(delegateBuilder, args);
                                if (method.getName().equals("build"))
                                    log.info("Builder "+delegateBuilder+" provided query: "+result);
                                return result;
                            }
                        }
                    );
                    return logResult(result);
                } catch (Throwable t){
                    throw logException(t);
                }
            }
        };
    }

    @Override
    public Mono<Success> optimizePages(SuggestionStrength strength, OptimizationType type) {
        return delegate.optimizePages(strength, type);
    }

    @Override
    public Flux<String> getManagedCollections() {
        return null;
    }

    @Override
    public Mono<Success> manage(String collectionName) {
        return null;
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

    @Override
    public TemporalService getTemporalService() {
        return null;
    }
}
