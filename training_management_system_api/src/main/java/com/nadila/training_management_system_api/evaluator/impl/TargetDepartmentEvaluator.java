package com.nadila.training_management_system_api.evaluator.impl;

import com.nadila.training_management_system_api.entity.EligibilityRule;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import com.nadila.training_management_system_api.evaluator.EligibilityRuleEvaluator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TargetDepartmentEvaluator implements EligibilityRuleEvaluator {

    @Override
    public boolean supports(EligibilityRuleType ruleType) {
        return ruleType == EligibilityRuleType.TARGET_DEPARTMENT;
    }

    @Override
    public String evaluate(EligibilityRule rule, Officer officer, TrainingProgramme programme) {
        if (officer.getDepartment() == null) {
            return "Officer is not assigned to any department.";
        }

        String ruleVal = rule.getRuleValue();
        if (!StringUtils.hasText(ruleVal)) {
            return null;
        }

        Set<String> allowed = Arrays.stream(ruleVal.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        String officerDeptName = officer.getDepartment().getName();
        String officerDeptIdStr = String.valueOf(officer.getDepartment().getDepartmentId());

        boolean matched = allowed.stream().anyMatch(val ->
                val.equalsIgnoreCase(officerDeptIdStr) ||
                officerDeptName.equalsIgnoreCase(val) ||
                officerDeptName.toLowerCase().contains(val.toLowerCase())
        );

        if (!matched) {
            if (StringUtils.hasText(rule.getCustomErrorMessage())) {
                return rule.getCustomErrorMessage();
            }
            return String.format("Department '%s' is not eligible for this programme. Allowed departments: %s",
                    officerDeptName, ruleVal);
        }

        return null;
    }
}
