package com.github.filipmalczak.vent.web.integration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Locale;

@Configuration
public class WebServerConfiguration {

    @Bean
    public Converters converters(){
        return new Converters();
    }

    @Bean
    public Formatter<LocalDateTime> localDateFormatter() {
        return new Formatter<LocalDateTime>() {
            @Override
            public LocalDateTime parse(String text, Locale locale) throws ParseException {
                return LocalDateTime.parse(text, DateFormat.QUERY_AT);
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return DateFormat.QUERY_AT.format(object);
            }
        };
    }
}
