package com.nadila.training_management_system_api.service.impl;

import com.nadila.training_management_system_api.dto.request.TrainingProgrammeRequest;
import com.nadila.training_management_system_api.dto.response.DepartmentResponse;
import com.nadila.training_management_system_api.dto.response.TrainerResponse;
import com.nadila.training_management_system_api.dto.response.TrainingProgrammeResponse;
import com.nadila.training_management_system_api.dto.response.VenueResponse;
import com.nadila.training_management_system_api.entity.Department;
import com.nadila.training_management_system_api.entity.Trainer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.entity.Venue;
import com.nadila.training_management_system_api.enums.NominationStatus;
import com.nadila.training_management_system_api.exception.ResourceNotFoundException;
import com.nadila.training_management_system_api.repository.DepartmentRepository;
import com.nadila.training_management_system_api.repository.NominationRepository;
import com.nadila.training_management_system_api.repository.TrainerRepository;
import com.nadila.training_management_system_api.repository.TrainingProgrammeRepository;
import com.nadila.training_management_system_api.repository.VenueRepository;
import com.nadila.training_management_system_api.service.TrainingProgrammeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingProgrammeServiceImpl implements TrainingProgrammeService {

    private final TrainingProgrammeRepository programmeRepository;
    private final VenueRepository venueRepository;
    private final TrainerRepository trainerRepository;
    private final DepartmentRepository departmentRepository;
    private final NominationRepository nominationRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TrainingProgrammeResponse create(TrainingProgrammeRequest request) {
        TrainingProgramme programme = TrainingProgramme.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .maxParticipants(request.getMaxParticipants())
                .venue(resolveVenue(request.getVenueId()))
                .trainer(resolveTrainer(request.getTrainerId()))
                .targetDepartments(resolveDepartments(request.getTargetDepartmentIds()))
                .build();

        if (request.getEligibilityRules() != null && !request.getEligibilityRules().isEmpty()) {
            List<com.nadila.training_management_system_api.entity.EligibilityRule> rules = request.getEligibilityRules().stream()
                    .map(r -> com.nadila.training_management_system_api.entity.EligibilityRule.builder()
                            .programme(programme)
                            .ruleType(r.getRuleType())
                            .ruleValue(r.getRuleValue())
                            .customErrorMessage(r.getCustomErrorMessage())
                            .build())
                    .collect(Collectors.toList());
            programme.setEligibilityRules(rules);
        }

        return toResponse(programmeRepository.save(programme));
    }

    @Override
    @Transactional
    public TrainingProgrammeResponse update(Long programmeId, TrainingProgrammeRequest request) {
        TrainingProgramme programme = getEntity(programmeId);
        programme.setTitle(request.getTitle());
        programme.setDescription(request.getDescription());
        programme.setStartDate(request.getStartDate());
        programme.setEndDate(request.getEndDate());
        programme.setMaxParticipants(request.getMaxParticipants());
        programme.setVenue(resolveVenue(request.getVenueId()));
        programme.setTrainer(resolveTrainer(request.getTrainerId()));
        programme.setTargetDepartments(resolveDepartments(request.getTargetDepartmentIds()));

        programme.getEligibilityRules().clear();
        if (request.getEligibilityRules() != null && !request.getEligibilityRules().isEmpty()) {
            for (var r : request.getEligibilityRules()) {
                programme.getEligibilityRules().add(
                        com.nadila.training_management_system_api.entity.EligibilityRule.builder()
                                .programme(programme)
                                .ruleType(r.getRuleType())
                                .ruleValue(r.getRuleValue())
                                .customErrorMessage(r.getCustomErrorMessage())
                                .build()
                );
            }
        }

        return toResponse(programmeRepository.save(programme));
    }

    @Override
    public TrainingProgrammeResponse getById(Long programmeId) {
        return toResponse(getEntity(programmeId));
    }

    @Override
    public List<TrainingProgrammeResponse> getAll() {
        return programmeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long programmeId) {
        programmeRepository.delete(getEntity(programmeId));
    }

    private TrainingProgramme getEntity(Long programmeId) {
        return programmeRepository.findById(programmeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Training programme not found with id: " + programmeId));
    }

    private Venue resolveVenue(Long venueId) {
        if (venueId == null) return null;
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + venueId));
    }

    private Trainer resolveTrainer(Long trainerId) {
        if (trainerId == null) return null;
        return trainerRepository.findById(trainerId)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + trainerId));
    }

    private Set<Department> resolveDepartments(Set<Long> departmentIds) {
        if (departmentIds == null || departmentIds.isEmpty()) return new HashSet<>();
        Set<Department> departments = new HashSet<>(departmentRepository.findAllById(departmentIds));
        if (departments.size() != departmentIds.size()) {
            throw new ResourceNotFoundException("One or more target departments were not found");
        }
        return departments;
    }

    private TrainingProgrammeResponse toResponse(TrainingProgramme programme) {
        long approved = nominationRepository.countByProgramme_ProgrammeIdAndStatus(
                programme.getProgrammeId(), NominationStatus.APPROVED);

        List<com.nadila.training_management_system_api.dto.response.EligibilityRuleResponse> rules =
                programme.getEligibilityRules() == null ? List.of() : programme.getEligibilityRules().stream()
                        .map(r -> com.nadila.training_management_system_api.dto.response.EligibilityRuleResponse.builder()
                                .ruleId(r.getRuleId())
                                .ruleType(r.getRuleType())
                                .ruleValue(r.getRuleValue())
                                .customErrorMessage(r.getCustomErrorMessage())
                                .build())
                        .toList();

        return TrainingProgrammeResponse.builder()
                .programmeId(programme.getProgrammeId())
                .title(programme.getTitle())
                .description(programme.getDescription())
                .startDate(programme.getStartDate())
                .endDate(programme.getEndDate())
                .venue(programme.getVenue() == null ? null : modelMapper.map(programme.getVenue(), VenueResponse.class))
                .trainer(programme.getTrainer() == null ? null : modelMapper.map(programme.getTrainer(), TrainerResponse.class))
                .maxParticipants(programme.getMaxParticipants())
                .approvedCount((int) approved)
                .availableSeats(Math.max(0, programme.getMaxParticipants() - (int) approved))
                .targetDepartments(programme.getTargetDepartments().stream()
                        .map(d -> modelMapper.map(d, DepartmentResponse.class))
                        .collect(Collectors.toSet()))
                .eligibilityRules(rules)
                .build();
    }
}
