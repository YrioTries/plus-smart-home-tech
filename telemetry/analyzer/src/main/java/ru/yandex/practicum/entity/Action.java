package ru.yandex.practicum.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "actions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer value;
}
