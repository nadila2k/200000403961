package com.nadila.training_management_system_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficerResponse {
    private Long officerId;
    private String fullName;
    private String nic;
    private Long departmentId;
    private String departmentName;
    private String designation;
    private String email;
    private String phone;
}
