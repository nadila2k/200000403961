package com.nadila.training_management_system_api.service.impl;

import com.nadila.training_management_system_api.dto.response.EligibilityCheckResponse;
import com.nadila.training_management_system_api.entity.EligibilityRule;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.EligibilityRuleType;
import com.nadila.training_management_system_api.evaluator.EligibilityRuleEvaluator;
import com.nadila.training_management_system_api.exception.ResourceNotFoundException;
import com.nadila.training_management_system_api.repository.OfficerRepository;
import com.nadila.training_management_system_api.repository.TrainingProgrammeRepository;
import com.nadila.training_management_system_api.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityServiceImpl implements EligibilityService {

    private final List<EligibilityRuleEvaluator> evaluators;
    private final TrainingProgrammeRepository programmeRepository;
    private final OfficerRepository officerRepository;

    @Override
    @Transactional(readOnly = true)
    public EligibilityCheckResponse checkEligibility(Long programmeId, Long officerId) {
        TrainingProgramme programme = programmeRepository.findById(programmeId)
                .orElseThrow(() -> new ResourceNotFoundException("Training programme not found with id: " + programmeId));

        Officer officer = officerRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + officerId));

        return evaluateEligibility(programme, officer);
    }

    @Override
    public EligibilityCheckResponse evaluateEligibility(TrainingProgramme programme, Officer officer) {
        List<String> failureReasons = new ArrayList<>();

        // 1. Evaluate explicit rules defined on programme
        if (programme.getEligibilityRules() != null) {
            for (EligibilityRule rule : programme.getEligibilityRules()) {
                for (EligibilityRuleEvaluator evaluator : evaluators) {
                    if (evaluator.supports(rule.getRuleType())) {
                        String error = evaluator.evaluate(rule, officer, programme);
                        if (error != null) {
                            failureReasons.add(error);
                        }
                    }
                }
            }
        }

        // 2. Backward compatibility: targetDepartments check if targetDepartments is defined and no explicit TARGET_DEPARTMENT rule exists
        boolean hasTargetDeptRule = programme.getEligibilityRules() != null && programme.getEligibilityRules().stream()
                .anyMatch(r -> r.getRuleType() == EligibilityRuleType.TARGET_DEPARTMENT);

        if (!hasTargetDeptRule && programme.getTargetDepartments() != null && !programme.getTargetDepartments().isEmpty()) {
            if (officer.getDepartment() == null || !programme.getTargetDepartments().contains(officer.getDepartment())) {
                String officerDeptName = officer.getDepartment() != null ? officer.getDepartment().getName() : "None";
                failureReasons.add(String.format("Officer department '%s' is not in target departments for this programme.", officerDeptName));
            }
        }

        boolean eligible = failureReasons.isEmpty();

        return EligibilityCheckResponse.builder()
                .eligible(eligible)
                .programmeId(programme.getProgrammeId())
                .programmeTitle(programme.getTitle())
                .officerId(officer.getOfficerId())
                .officerName(officer.getFullName())
                .failureReasons(failureReasons)
                .build();
    }
}
