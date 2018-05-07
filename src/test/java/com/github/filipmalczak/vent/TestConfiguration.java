package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.embedded.EmbeddedReactiveVentDb;
import com.github.filipmalczak.vent.embedded.service.TemporalService;
import com.github.filipmalczak.vent.testimpl.LoggingDbWrapper;
import com.github.filipmalczak.vent.testimpl.StackTracer;
import com.github.filipmalczak.vent.testimpl.TestingTemporalService;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
public class TestConfiguration {
    private static final boolean EXTENSIVE_LOGGING = false;

    public static class ExtensiveLoggingCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return EXTENSIVE_LOGGING;
        }
    }

    @Bean
    public TemporalService temporalService(){
        return new TestingTemporalService();
    }

    @Conditional(ExtensiveLoggingCondition.class)
    @Bean
    public StackTracer stackTracer(){
        return StackTracer.builder().basePackageClass(VentApplication.class).build();
    }

    @Conditional(ExtensiveLoggingCondition.class)
    @Bean
    public ReactiveVentDb reactiveVentDb(EmbeddedReactiveVentDb embeddedReactiveVentDb, StackTracer stackTracer){
        return LoggingDbWrapper.defaultWrapper(stackTracer, embeddedReactiveVentDb);
    }
}
