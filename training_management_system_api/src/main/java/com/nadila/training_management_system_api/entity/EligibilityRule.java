package com.nadila.training_management_system_api.entity;

import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "eligibility_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ruleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_id", nullable = false)
    private TrainingProgramme programme;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityRuleType ruleType;

    @Column(nullable = false, length = 1000)
    private String ruleValue;

    private String customErrorMessage;
}
