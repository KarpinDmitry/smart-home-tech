package ru.yandex.practicum.model;

import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ScenarioActionId implements Serializable {
    private Long scenarioId;
    private String sensorId;
    private Long actionId;
}
