package com.nadila.training_management_system_api.dto.response;

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
public class TrainingProgrammeResponse {
    private Long programmeId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private VenueResponse venue;
    private TrainerResponse trainer;
    private Integer maxParticipants;
    private Integer approvedCount;
    private Integer availableSeats;
    private Set<DepartmentResponse> targetDepartments;
    private java.util.List<EligibilityRuleResponse> eligibilityRules;
}
