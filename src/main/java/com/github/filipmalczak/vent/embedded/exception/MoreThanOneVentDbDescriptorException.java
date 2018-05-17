package com.github.filipmalczak.vent.embedded.exception;

import com.github.filipmalczak.vent.embedded.model.VentDbDescriptor;
import lombok.Getter;

import java.util.List;

public class MoreThanOneVentDbDescriptorException extends IllegalVentStateException {
    @Getter private List<VentDbDescriptor> descriptors;

    public MoreThanOneVentDbDescriptorException(List<VentDbDescriptor> descriptors) {
        super("More than one Vent DB descriptor present in backing Mongo instance: "+descriptors);
        this.descriptors = descriptors;
    }
}
