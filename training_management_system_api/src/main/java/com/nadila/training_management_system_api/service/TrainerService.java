package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.TrainerRequest;
import com.nadila.training_management_system_api.dto.response.TrainerResponse;

import java.util.List;

public interface TrainerService {
    TrainerResponse create(TrainerRequest request);
    TrainerResponse update(Long trainerId, TrainerRequest request);
    TrainerResponse getById(Long trainerId);
    List<TrainerResponse> getAll();
    void delete(Long trainerId);
}
