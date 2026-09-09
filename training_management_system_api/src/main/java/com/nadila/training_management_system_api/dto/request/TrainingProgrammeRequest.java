package com.nadila.training_management_system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgrammeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private Long venueId;

    private Long trainerId;

    @NotNull(message = "Maximum participants is required")
    @Positive(message = "Maximum participants must be positive")
    private Integer maxParticipants;

    private Set<Long> targetDepartmentIds;

    private java.util.List<EligibilityRuleRequest> eligibilityRules;
}
