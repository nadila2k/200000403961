package com.nadila.training_management_system_api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NominationRequest {

    @NotNull(message = "Programme id is required")
    private Long programmeId;

    @NotNull(message = "Officer id is required")
    private Long officerId;

    @NotNull(message = "Nominating department id is required")
    private Long nominatingDepartmentId;
}
