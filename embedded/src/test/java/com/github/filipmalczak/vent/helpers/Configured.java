package com.github.filipmalczak.vent.helpers;

import com.github.filipmalczak.vent.mongo.impl.optimization.SimpleDurationDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("x.y")
public class Configured extends SimpleDurationDefinition {
}
