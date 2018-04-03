package com.github.filipmalczak.vent.model;


import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class VentInterval {
    private Map snapshot;
    private List<Vent> events;
}
