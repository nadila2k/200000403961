package com.nadila.training_management_system_api.service.impl;

import com.nadila.training_management_system_api.dto.request.DepartmentRequest;
import com.nadila.training_management_system_api.dto.response.DepartmentResponse;
import com.nadila.training_management_system_api.entity.Department;
import com.nadila.training_management_system_api.exception.ResourceAlreadyExistsException;
import com.nadila.training_management_system_api.exception.ResourceNotFoundException;
import com.nadila.training_management_system_api.repository.DepartmentRepository;
import com.nadila.training_management_system_api.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ResourceAlreadyExistsException(
                    "A department named '" + request.getName() + "' already exists");
        }
        Department department = modelMapper.map(request, Department.class);
        Department saved = departmentRepository.save(department);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long departmentId, DepartmentRequest request) {
        Department department = getEntity(departmentId);
        department.setName(request.getName());
        department.setFocalPointEmail(request.getFocalPointEmail());
        department.setFocalPointPhone(request.getFocalPointPhone());
        return toResponse(departmentRepository.save(department));
    }

    @Override
    public DepartmentResponse getById(Long departmentId) {
        return toResponse(getEntity(departmentId));
    }

    @Override
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long departmentId) {
        Department department = getEntity(departmentId);
        departmentRepository.delete(department);
    }

    private Department getEntity(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + departmentId));
    }

    private DepartmentResponse toResponse(Department department) {
        return modelMapper.map(department, DepartmentResponse.class);
    }
}
