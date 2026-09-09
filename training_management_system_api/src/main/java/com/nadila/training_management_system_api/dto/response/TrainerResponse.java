package com.nadila.training_management_system_api.dto.response;

import com.nadila.training_management_system_api.enums.TrainerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerResponse {
    private Long trainerId;
    private String fullName;
    private TrainerType type;
    private String specialization;
    private String organization;
    private String email;
    private String phone;
}
