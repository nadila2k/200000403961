package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.response.EligibilityCheckResponse;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;

public interface EligibilityService {

    EligibilityCheckResponse evaluateEligibility(TrainingProgramme programme, Officer officer);

    EligibilityCheckResponse checkEligibility(Long programmeId, Long officerId);
}
