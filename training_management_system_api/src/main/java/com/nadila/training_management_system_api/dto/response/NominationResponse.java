package com.nadila.training_management_system_api.dto.response;

import com.nadila.training_management_system_api.enums.NominationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NominationResponse {
    private Long nominationId;
    private Long programmeId;
    private String programmeTitle;
    private Long officerId;
    private String officerName;
    private Long nominatingDepartmentId;
    private String nominatingDepartmentName;
    private NominationStatus status;
    private LocalDateTime nominatedAt;
}
