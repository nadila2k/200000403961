package com.nadila.training_management_system_api.dto.request;

import com.nadila.training_management_system_api.enums.EligibilityRuleType;
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
public class EligibilityRuleRequest {

    @NotNull(message = "Rule type is required")
    private EligibilityRuleType ruleType;

    @NotBlank(message = "Rule value is required")
    private String ruleValue;

    private String customErrorMessage;
}
