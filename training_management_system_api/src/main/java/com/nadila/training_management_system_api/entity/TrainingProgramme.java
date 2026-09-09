package com.nadila.training_management_system_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "training_programmes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingProgramme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programmeId;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Column(nullable = false)
    private Integer maxParticipants;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "programme_target_departments",
            joinColumns = @JoinColumn(name = "programme_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> targetDepartments = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "programme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nomination> nominations = new java.util.ArrayList<>();
}
