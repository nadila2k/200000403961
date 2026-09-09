package com.nadila.training_management_system_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityCheckResponse {
    private boolean eligible;
    private Long programmeId;
    private String programmeTitle;
    private Long officerId;
    private String officerName;
    private List<String> failureReasons;
}
