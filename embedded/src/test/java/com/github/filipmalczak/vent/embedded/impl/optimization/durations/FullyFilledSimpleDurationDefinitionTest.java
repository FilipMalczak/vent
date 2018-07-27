package com.github.filipmalczak.vent.embedded.impl.optimization.durations;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.helpers.Configured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@VentSpringTest
@TestPropertySource(properties = {"x.y.value=1", "x.y.unit=MINUTES"})
public class FullyFilledSimpleDurationDefinitionTest {
    @Autowired
    private Configured configured;

    @Test
    public void durationShouldBePresent(){
        assertEquals(Duration.ofMinutes(1), configured.getDuration().get());
    }
}