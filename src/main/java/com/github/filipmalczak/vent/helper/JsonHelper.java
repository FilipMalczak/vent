package com.github.filipmalczak.vent.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JsonHelper {
    private ObjectMapper objectMapper;
    private ObjectWriter writer;

    @PostConstruct
    public void init(){
        objectMapper = new ObjectMapper();
        writer = objectMapper.writerWithDefaultPrettyPrinter();
    }

    @SneakyThrows
    public <T> T toObject(String json, Class<T> clazz){
        return objectMapper.readValue(json, clazz);
    }

    @SneakyThrows
    public <T> String toJson(T object){
        return writer.writeValueAsString(object);
    }
}
