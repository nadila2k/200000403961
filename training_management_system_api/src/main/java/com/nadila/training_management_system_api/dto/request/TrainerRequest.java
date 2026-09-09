package com.nadila.training_management_system_api.dto.request;

import com.nadila.training_management_system_api.enums.TrainerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerRequest {

    @NotBlank(message = "Trainer name is required")
    private String fullName;

    @NotNull(message = "Trainer type is required")
    private TrainerType type;

    private String specialization;

    private String organization;

    private String email;

    private String phone;
}
