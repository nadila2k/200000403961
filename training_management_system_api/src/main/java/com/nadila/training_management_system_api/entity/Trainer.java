package com.nadila.training_management_system_api.entity;

import com.nadila.training_management_system_api.enums.TrainerType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainerId;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private TrainerType type;

    private String specialization;

    private String organization;

    private String email;

    private String phone;
}
