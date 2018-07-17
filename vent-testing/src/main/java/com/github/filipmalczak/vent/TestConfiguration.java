package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.testing.StackTracer;
import com.github.filipmalczak.vent.testing.TestingTemporalService;
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

    @Conditional(ExtensiveLoggingCondition.class)
    @Bean
    public StackTracer stackTracer(){
        return StackTracer.builder().basePackageClass(this.getClass()).build();
    }

    @Bean
    public TemporalService temporalService(){
        return new TestingTemporalService();
    }

    //todo: rethink logging
//    @Conditional(ExtensiveLoggingCondition.class)
//    @Bean
//    public ReactiveVentDb reactiveVentDb(ReactiveVentDb embeddedReactiveVentDb, StackTracer stackTracer){
//        return LoggingDbWrapper.defaultWrapper(stackTracer, embeddedReactiveVentDb);
//    }
}
