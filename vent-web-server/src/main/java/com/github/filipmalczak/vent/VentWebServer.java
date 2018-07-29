package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.api.reactive.ReactiveVentDb;
import com.github.filipmalczak.vent.mongo.VentDb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import static java.util.Arrays.asList;

@SpringBootApplication(scanBasePackageClasses = {VentWebServer.class, ReactiveVentDb.class, VentDb.class})

@EnableAutoConfiguration
@EnableReactiveMongoRepositories(basePackageClasses = VentDb.class)
@ImportAutoConfiguration({
    MongoReactiveAutoConfiguration.class,
    MongoReactiveDataAutoConfiguration.class,
    MongoDataAutoConfiguration.class
})

@Slf4j
public class VentWebServer {
	//if you want to customize Vent, change ReactiveMongoOperations bean

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(VentWebServer.class, args);
		log.info(""+asList(ctx.getBeanDefinitionNames()));
	}
}
