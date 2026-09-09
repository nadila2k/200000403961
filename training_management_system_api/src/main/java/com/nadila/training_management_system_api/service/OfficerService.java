package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.OfficerRequest;
import com.nadila.training_management_system_api.dto.response.OfficerResponse;

import java.util.List;

public interface OfficerService {
    OfficerResponse create(OfficerRequest request);
    OfficerResponse update(Long officerId, OfficerRequest request);
    OfficerResponse getById(Long officerId);
    List<OfficerResponse> getAll();
    List<OfficerResponse> getByDepartment(Long departmentId);
    void delete(Long officerId);
}
