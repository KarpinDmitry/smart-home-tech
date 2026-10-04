package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "actions")
@Getter
@Setter
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    private String sensorId;

    @Enumerated(EnumType.STRING)
    private ActionType type;

    private Integer value;
}
