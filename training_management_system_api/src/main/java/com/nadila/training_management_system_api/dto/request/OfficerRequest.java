package com.nadila.training_management_system_api.dto.request;

import jakarta.validation.constraints.Email;
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
public class OfficerRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String nic;

    @NotNull(message = "Department id is required")
    private Long departmentId;

    private String designation;

    private String grade;

    private java.time.LocalDate serviceStartDate;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;
}
