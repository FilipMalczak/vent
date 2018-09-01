package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.helper.StackTracer;
import com.mongodb.MongoClient;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.time.LocalDateTime;

@Configuration
@EnableReactiveMongoRepositories
@ImportAutoConfiguration({
    MongoReactiveAutoConfiguration.class,
    MongoReactiveDataAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})
public class MongoVentWithSpringDataConfiguration extends RequiredCodecsForMongoVent {

    @Bean
    public MongoClient mongoClient(){
        //todo work on random port for tests
        int port = Integer.parseInt(System.getProperty("project.mongo.port", "27017"));
        return new MongoClient("localhost", port);
    }

    @Bean
    public CodecRegistry codecRegistry(Codec<LocalDateTime> localDateTimeCodec, ReactiveMongoTemplate template) {
        return super.codecRegistry(localDateTimeCodec, template.getMongoDatabase().getCodecRegistry());
    }

    @Override
    @Bean
    public Codec<LocalDateTime> localDateTimeCodec(TemporalService temporalService) {
        return super.localDateTimeCodec(temporalService);
    }

    @Bean
    public StackTracer stackTracer(){
        return StackTracer.builder().basePackageClass(VentDb.class).build();
    }
}
