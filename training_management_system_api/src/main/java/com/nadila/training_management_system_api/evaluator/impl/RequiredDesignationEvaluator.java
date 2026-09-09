package com.nadila.training_management_system_api.evaluator.impl;

import com.nadila.training_management_system_api.entity.EligibilityRule;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import com.nadila.training_management_system_api.evaluator.EligibilityRuleEvaluator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Component
public class RequiredDesignationEvaluator implements EligibilityRuleEvaluator {

    @Override
    public boolean supports(EligibilityRuleType ruleType) {
        return ruleType == EligibilityRuleType.REQUIRED_DESIGNATION;
    }

    @Override
    public String evaluate(EligibilityRule rule, Officer officer, TrainingProgramme programme) {
        String officerDesig = officer.getDesignation();
        if (!StringUtils.hasText(officerDesig)) {
            return String.format("Officer has no designation specified. Required designation: %s", rule.getRuleValue());
        }

        String ruleVal = rule.getRuleValue();
        if (!StringUtils.hasText(ruleVal)) {
            return null;
        }

        boolean matched = Arrays.stream(ruleVal.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .anyMatch(val -> officerDesig.equalsIgnoreCase(val) || officerDesig.toLowerCase().contains(val.toLowerCase()));

        if (!matched) {
            if (StringUtils.hasText(rule.getCustomErrorMessage())) {
                return rule.getCustomErrorMessage();
            }
            return String.format("Designation '%s' is not eligible. Required designation: %s", officerDesig, ruleVal);
        }

        return null;
    }
}
