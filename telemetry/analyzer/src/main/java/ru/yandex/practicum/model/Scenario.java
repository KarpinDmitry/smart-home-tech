package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "scenarios", uniqueConstraints = @UniqueConstraint(columnNames = {"hub_id", "name"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Scenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hub_id", nullable = false)
    private String hubId;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "scenario", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<ScenarioCondition> conditions = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "scenario", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private Set<ScenarioAction> actions = new HashSet<>();

}
