package com.github.filipmalczak.vent.embedded;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import com.mongodb.MongoClient;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class EmbeddedVentConfiguration {

    @Bean
    public CodecRegistry codecRegistry(Codec<LocalDateTime> localDateTimeCodec, MongoClient mongoClient){
        return CodecRegistries.fromRegistries(
            CodecRegistries.fromCodecs(localDateTimeCodec),
            mongoClient.getMongoClientOptions().getCodecRegistry()
        );
    }

    @Bean
    public Codec<LocalDateTime> localDateTimeCodec(TemporalService temporalService){
        return new Codec<LocalDateTime>() {
            @Override
            public LocalDateTime decode(BsonReader reader, DecoderContext decoderContext) {
                return temporalService.fromTimestamp(reader.readDateTime());
            }

            @Override
            public void encode(BsonWriter writer, LocalDateTime value, EncoderContext encoderContext) {
                writer.writeDateTime(temporalService.toTimestamp(value));
            }

            @Override
            public Class<LocalDateTime> getEncoderClass() {
                return LocalDateTime.class;
            }
        };
    }
}
