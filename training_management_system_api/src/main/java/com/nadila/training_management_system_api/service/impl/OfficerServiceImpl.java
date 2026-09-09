package com.nadila.training_management_system_api.service.impl;

import com.nadila.training_management_system_api.dto.request.OfficerRequest;
import com.nadila.training_management_system_api.dto.response.OfficerResponse;
import com.nadila.training_management_system_api.entity.Department;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.exception.ResourceAlreadyExistsException;
import com.nadila.training_management_system_api.exception.ResourceNotFoundException;
import com.nadila.training_management_system_api.repository.DepartmentRepository;
import com.nadila.training_management_system_api.repository.OfficerRepository;
import com.nadila.training_management_system_api.service.OfficerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfficerServiceImpl implements OfficerService {

    private final OfficerRepository officerRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public OfficerResponse create(OfficerRequest request) {
        if (StringUtils.hasText(request.getNic()) && officerRepository.existsByNic(request.getNic())) {
            throw new ResourceAlreadyExistsException(
                    "An officer with NIC '" + request.getNic() + "' already exists");
        }
        Department department = getDepartment(request.getDepartmentId());

        Officer officer = Officer.builder()
                .fullName(request.getFullName())
                .nic(request.getNic())
                .department(department)
                .designation(request.getDesignation())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        return toResponse(officerRepository.save(officer));
    }

    @Override
    @Transactional
    public OfficerResponse update(Long officerId, OfficerRequest request) {
        Officer officer = getEntity(officerId);
        Department department = getDepartment(request.getDepartmentId());

        officer.setFullName(request.getFullName());
        officer.setNic(request.getNic());
        officer.setDepartment(department);
        officer.setDesignation(request.getDesignation());
        officer.setEmail(request.getEmail());
        officer.setPhone(request.getPhone());

        return toResponse(officerRepository.save(officer));
    }

    @Override
    public OfficerResponse getById(Long officerId) {
        return toResponse(getEntity(officerId));
    }

    @Override
    public List<OfficerResponse> getAll() {
        return officerRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public List<OfficerResponse> getByDepartment(Long departmentId) {
        return officerRepository.findByDepartment_DepartmentId(departmentId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long officerId) {
        officerRepository.delete(getEntity(officerId));
    }

    private Officer getEntity(Long officerId) {
        return officerRepository.findById(officerId)
                .orElseThrow(() -> new ResourceNotFoundException("Officer not found with id: " + officerId));
    }

    private Department getDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + departmentId));
    }

    private OfficerResponse toResponse(Officer officer) {
        return OfficerResponse.builder()
                .officerId(officer.getOfficerId())
                .fullName(officer.getFullName())
                .nic(officer.getNic())
                .departmentId(officer.getDepartment().getDepartmentId())
                .departmentName(officer.getDepartment().getName())
                .designation(officer.getDesignation())
                .email(officer.getEmail())
                .phone(officer.getPhone())
                .build();
    }
}
