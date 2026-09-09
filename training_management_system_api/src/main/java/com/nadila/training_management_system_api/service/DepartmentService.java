package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.DepartmentRequest;
import com.nadila.training_management_system_api.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse update(Long departmentId, DepartmentRequest request);
    DepartmentResponse getById(Long departmentId);
    List<DepartmentResponse> getAll();
    void delete(Long departmentId);
}
