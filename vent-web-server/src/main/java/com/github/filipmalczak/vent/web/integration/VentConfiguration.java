package com.github.filipmalczak.vent.web.integration;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.github.filipmalczak.vent.mongo.ReactiveMongoVentFactory;
import com.github.filipmalczak.vent.mongo.RequiredCodecsForMongoVent;
import com.github.filipmalczak.vent.mongo.service.SimpleTemporalService;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.time.LocalDateTime;

@Configuration
public class VentConfiguration extends RequiredCodecsForMongoVent {
    @Autowired
    private ReactiveMongoTemplate template;

    @Bean
    public CodecRegistry codecRegistry(Codec<LocalDateTime> localDateTimeCodec) {
//        return super.codecRegistry(localDateTimeCodec, MongoClient.getDefaultCodecRegistry());
        return super.codecRegistry(localDateTimeCodec, template.getMongoDatabase().getCodecRegistry());
    }

    @Bean
    @Override
    public Codec<LocalDateTime> localDateTimeCodec(TemporalService temporalService) {
        return super.localDateTimeCodec(temporalService);
    }

    @Bean
    public ReactiveVentDb reactiveVentDb(TemporalService temporalService){
        return new ReactiveMongoVentFactory().
            reactiveMongoOperations(() -> template).
            temporalService(() -> temporalService).
            newInstance();
    }

    //todo figure out where to put this
    //fixme maybe inject this to reactiveVentDb as optional?
    @Bean
    public TemporalService temporalService(){
        return new SimpleTemporalService();
    }
}
