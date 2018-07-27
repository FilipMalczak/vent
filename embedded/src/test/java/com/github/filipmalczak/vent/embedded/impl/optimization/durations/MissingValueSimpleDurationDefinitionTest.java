package com.github.filipmalczak.vent.embedded.impl.optimization.durations;

import com.github.filipmalczak.vent.VentSpringTest;
import com.github.filipmalczak.vent.helpers.Configured;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@VentSpringTest
@TestPropertySource(properties = {"x.y.unit=MINUTES"})
public class MissingValueSimpleDurationDefinitionTest {
    @Autowired
    private Configured configured;

    @Test
    public void durationShouldNotBePresent(){
        assertFalse(configured.getDuration().isPresent());
        }

    @Test
    public void unitShouldStillBeAvailable(){
        assertEquals(MINUTES, configured.getUnit());
    }

    //fixme I know, I know, I should also check whether getValue() yields null; its overkill, though, so fuck it for now
}