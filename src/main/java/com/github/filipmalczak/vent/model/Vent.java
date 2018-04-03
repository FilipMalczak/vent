package com.github.filipmalczak.vent.model;

import com.github.filipmalczak.vent.dto.Operation;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Vent {
    private Operation operation;
    private Map payload;
    private LocalDateTime timestamp;
}
