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
public class RequiredGradeEvaluator implements EligibilityRuleEvaluator {

    @Override
    public boolean supports(EligibilityRuleType ruleType) {
        return ruleType == EligibilityRuleType.REQUIRED_GRADE;
    }

    @Override
    public String evaluate(EligibilityRule rule, Officer officer, TrainingProgramme programme) {
        String officerGrade = officer.getGrade();
        if (!StringUtils.hasText(officerGrade)) {
            return String.format("Officer has no grade specified. Required grade: %s", rule.getRuleValue());
        }

        String ruleVal = rule.getRuleValue();
        if (!StringUtils.hasText(ruleVal)) {
            return null;
        }

        boolean matched = Arrays.stream(ruleVal.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .anyMatch(val -> officerGrade.equalsIgnoreCase(val));

        if (!matched) {
            if (StringUtils.hasText(rule.getCustomErrorMessage())) {
                return rule.getCustomErrorMessage();
            }
            return String.format("Grade '%s' is not eligible. Required grade: %s", officerGrade, ruleVal);
        }

        return null;
    }
}
