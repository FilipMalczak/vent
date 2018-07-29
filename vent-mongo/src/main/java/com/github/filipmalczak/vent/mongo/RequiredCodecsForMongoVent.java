package com.github.filipmalczak.vent.mongo;

import com.github.filipmalczak.vent.api.temporal.TemporalService;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequiredCodecsForMongoVent {
    public CodecRegistry codecRegistry(Codec<LocalDateTime> localDateTimeCodec, CodecRegistry... registries){
        return CodecRegistries.fromRegistries(
            Stream.concat(
                Stream.of(CodecRegistries.fromCodecs(localDateTimeCodec)),
                Stream.of(registries)
            ).collect(Collectors.toList())

        );
    }

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
