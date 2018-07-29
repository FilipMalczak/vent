package com.github.filipmalczak.vent.mongo.impl.optimization.durations;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.helpers.Configured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@VentSpringTest
@TestPropertySource(properties = {"x.y.value=1"})
public class MissingUnitSimpleDurationDefinitionTest {
    @Autowired
    private Configured configured;

    @Test
    public void durationShouldNotBePresent(){
        assertFalse(configured.getDuration().isPresent());
        }

    @Test
    public void unitShouldStillBeAvailable(){
        assertEquals(Integer.valueOf(1), configured.getValue());
    }
}