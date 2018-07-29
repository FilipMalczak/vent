package com.github.filipmalczak.vent;

import com.github.filipmalczak.vent.mongo.RequiredCodecsForMongoVent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Inherited
@Retention(RUNTIME)
@Target(TYPE)
@SpringJUnitConfig({RequiredCodecsForMongoVent.class, TestConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public @interface VentSpringTest {
}
