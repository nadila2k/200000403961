package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.TrainingProgrammeRequest;
import com.nadila.training_management_system_api.dto.response.TrainingProgrammeResponse;

import java.util.List;

public interface TrainingProgrammeService {
    TrainingProgrammeResponse create(TrainingProgrammeRequest request);
    TrainingProgrammeResponse update(Long programmeId, TrainingProgrammeRequest request);
    TrainingProgrammeResponse getById(Long programmeId);
    List<TrainingProgrammeResponse> getAll();
    void delete(Long programmeId);
}
