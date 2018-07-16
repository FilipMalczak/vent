package com.github.filipmalczak.vent.web.integration;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfiguration {
    /*
     * TODO This makes it work only from gradle bootRun; make it more elastic
     */
    @Bean
    public MongoClient mongoClient(){
        int port = Integer.parseInt(System.getProperty("project.mongo.port", "27017"));
        return new MongoClient("localhost", port);
    }
}
