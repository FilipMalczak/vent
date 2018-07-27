package com.github.filipmalczak.vent.embedded.impl.optimization.durations;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.helpers.Configured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertFalse;

@VentSpringTest
public class MissingUnitAndValueSimpleDurationDefinitionTest {
    @Autowired
    private Configured configured;

    @Test
    public void durationShouldNotBePresent(){
        assertFalse(configured.getDuration().isPresent());
        }
}