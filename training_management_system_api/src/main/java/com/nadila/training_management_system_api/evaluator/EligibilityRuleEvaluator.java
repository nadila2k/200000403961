package com.nadila.training_management_system_api.evaluator;

import com.nadila.training_management_system_api.entity.EligibilityRule;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.EligibilityRuleType;

public interface EligibilityRuleEvaluator {

    boolean supports(EligibilityRuleType ruleType);

    /**
     * Evaluates a rule against an officer.
     * Returns null if officer is eligible, or returns error message if officer is ineligible.
     */
    String evaluate(EligibilityRule rule, Officer officer, TrainingProgramme programme);
}
