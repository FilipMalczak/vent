package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.embedded.EmbeddedVentCodecs;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
//@Import(EmbeddedVentCodecs.class)
//@ImportAutoConfiguration(EmbeddedVentConfiguration.class)
//@ImportAutoConfiguration(MongoReactiveDataAutoConfiguration.class)
@EnableReactiveMongoRepositories
public class VentWebServer {
	//if you want to customize Vent, change ReactiveMongoOperations bean

	public static void main(String[] args) {
		SpringApplication.run(VentWebServer.class, args);
	}
}
