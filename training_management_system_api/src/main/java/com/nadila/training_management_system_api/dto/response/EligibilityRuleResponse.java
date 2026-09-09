package com.nadila.training_management_system_api.dto.response;

import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityRuleResponse {
    private Long ruleId;
    private EligibilityRuleType ruleType;
    private String ruleValue;
    private String customErrorMessage;
}
