package com.nadila.training_management_system_api.evaluator.impl;

import com.nadila.training_management_system_api.entity.EligibilityRule;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import com.nadila.training_management_system_api.evaluator.EligibilityRuleEvaluator;
import org.springframework.stereotype.Component;

@Component
public class MinYearsOfServiceEvaluator implements EligibilityRuleEvaluator {

    @Override
    public boolean supports(EligibilityRuleType ruleType) {
        return ruleType == EligibilityRuleType.MIN_YEARS_OF_SERVICE;
    }

    @Override
    public String evaluate(EligibilityRule rule, Officer officer, TrainingProgramme programme) {
        int minYears;
        try {
            minYears = Integer.parseInt(rule.getRuleValue().trim());
        } catch (NumberFormatException e) {
            return null; // Ignore invalid rule config
        }

        int officerYears = officer.getYearsOfService();
        if (officerYears < minYears) {
            if (rule.getCustomErrorMessage() != null && !rule.getCustomErrorMessage().isBlank()) {
                return rule.getCustomErrorMessage();
            }
            return String.format("Minimum %d years of service required, but officer has %d year(s).",
                    minYears, officerYears);
        }

        return null;
    }
}
