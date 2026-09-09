package com.nadila.training_management_system_api.evaluator.impl;

import com.nadila.training_management_system_api.entity.EligibilityRule;
import com.nadila.training_management_system_api.entity.Nomination;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import com.nadila.training_management_system_api.enums.NominationStatus;
import com.nadila.training_management_system_api.evaluator.EligibilityRuleEvaluator;
import com.nadila.training_management_system_api.repository.NominationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RepeatCooldownEvaluator implements EligibilityRuleEvaluator {

    private final NominationRepository nominationRepository;

    @Override
    public boolean supports(EligibilityRuleType ruleType) {
        return ruleType == EligibilityRuleType.REPEAT_COOLDOWN_PERIOD;
    }

    @Override
    public String evaluate(EligibilityRule rule, Officer officer, TrainingProgramme programme) {
        int months = 12; // default
        try {
            if (rule.getRuleValue() != null && !rule.getRuleValue().isBlank()) {
                months = Integer.parseInt(rule.getRuleValue().trim());
            }
        } catch (NumberFormatException ignored) {
        }

        LocalDateTime cutoff = LocalDateTime.now().minusMonths(months);

        List<Nomination> pastNominations = nominationRepository.findByOfficer_OfficerId(officer.getOfficerId());

        boolean hasRecent = pastNominations.stream().anyMatch(nom -> {
            if (nom.getStatus() == NominationStatus.WITHDRAWN || nom.getStatus() == NominationStatus.REJECTED) {
                return false;
            }
            // Check if it's the exact same programme or same title
            boolean sameProg = nom.getProgramme().getProgrammeId().equals(programme.getProgrammeId())
                    || (nom.getProgramme().getTitle() != null && nom.getProgramme().getTitle().equalsIgnoreCase(programme.getTitle()));

            boolean withinCooldown = nom.getNominatedAt() != null && nom.getNominatedAt().isAfter(cutoff);
            return sameProg && withinCooldown;
        });

        if (hasRecent) {
            if (rule.getCustomErrorMessage() != null && !rule.getCustomErrorMessage().isBlank()) {
                return rule.getCustomErrorMessage();
            }
            return String.format("Officer has already participated in this training programme within the previous %d months.", months);
        }

        return null;
    }
}
