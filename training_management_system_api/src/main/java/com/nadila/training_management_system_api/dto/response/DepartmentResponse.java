package com.nadila.training_management_system_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponse {
    private Long departmentId;
    private String name;
    private String focalPointEmail;
    private String focalPointPhone;
}
