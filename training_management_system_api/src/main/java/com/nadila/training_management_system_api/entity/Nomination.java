package com.nadila.training_management_system_api.entity;

import com.nadila.training_management_system_api.enums.NominationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "nominations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nomination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nominationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private TrainingProgramme programme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id", nullable = false)
    private Officer officer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nominating_department_id", nullable = false)
    private Department nominatingDepartment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NominationStatus status;

    @Column(nullable = false)
    private LocalDateTime nominatedAt;
}
