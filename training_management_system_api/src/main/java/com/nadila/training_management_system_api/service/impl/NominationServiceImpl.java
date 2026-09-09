package com.nadila.training_management_system_api.service.impl;

import com.nadila.training_management_system_api.dto.request.NominationRequest;
import com.nadila.training_management_system_api.dto.response.NominationResponse;
import com.nadila.training_management_system_api.entity.Department;
import com.nadila.training_management_system_api.entity.Nomination;
import com.nadila.training_management_system_api.entity.Officer;
import com.nadila.training_management_system_api.entity.TrainingProgramme;
import com.nadila.training_management_system_api.enums.NominationStatus;
import com.nadila.training_management_system_api.exception.DuplicateNominationException;
import com.nadila.training_management_system_api.exception.ResourceNotFoundException;
import com.nadila.training_management_system_api.repository.DepartmentRepository;
import com.nadila.training_management_system_api.repository.NominationRepository;
import com.nadila.training_management_system_api.repository.OfficerRepository;
import com.nadila.training_management_system_api.repository.TrainingProgrammeRepository;
import com.nadila.training_management_system_api.service.NominationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NominationServiceImpl implements NominationService {

    private static final List<NominationStatus> ACTIVE_STATUSES =
            List.of(NominationStatus.PENDING, NominationStatus.APPROVED, NominationStatus.WAITLISTED);

    private final NominationRepository nominationRepository;
    private final TrainingProgrammeRepository programmeRepository;
    private final OfficerRepository officerRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public NominationResponse nominate(NominationRequest request) {

        TrainingProgramme programme = programmeRepository.findById(request.getProgrammeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Training programme not found with id: " + request.getProgrammeId()));

        Officer officer = officerRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Officer not found with id: " + request.getOfficerId()));

        Department nominatingDepartment = departmentRepository.findById(request.getNominatingDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getNominatingDepartmentId()));

        // ---- 1. DUPLICATE-NOMINATION CHECK ----
        // An officer may only have one ACTIVE (PENDING/APPROVED/WAITLISTED) nomination
        // per programme, no matter which department submits it. This is what stops
        // Finance and Administration both nominating the same officer for the same course.
        Optional<Nomination> existing = nominationRepository
                .findFirstByProgramme_ProgrammeIdAndOfficer_OfficerIdAndStatusIn(
                        programme.getProgrammeId(), officer.getOfficerId(), ACTIVE_STATUSES);

        if (existing.isPresent()) {
            Nomination duplicate = existing.get();
            throw new DuplicateNominationException(String.format(
                    "%s has already been nominated for '%s' by %s on %s (status: %s).",
                    officer.getFullName(),
                    programme.getTitle(),
                    duplicate.getNominatingDepartment().getName(),
                    duplicate.getNominatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    duplicate.getStatus()
            ));
        }

        // ---- 2. CAPACITY CHECK -> auto-waitlist if programme is full ----
        long approvedCount = nominationRepository.countByProgramme_ProgrammeIdAndStatus(
                programme.getProgrammeId(), NominationStatus.APPROVED);

        NominationStatus initialStatus = approvedCount >= programme.getMaxParticipants()
                ? NominationStatus.WAITLISTED
                : NominationStatus.PENDING;

        Nomination nomination = Nomination.builder()
                .programme(programme)
                .officer(officer)
                .nominatingDepartment(nominatingDepartment)
                .status(initialStatus)
                .nominatedAt(LocalDateTime.now())
                .build();

        return toResponse(nominationRepository.save(nomination));
    }

    @Override
    @Transactional
    public NominationResponse updateStatus(Long nominationId, String status) {
        Nomination nomination = getEntity(nominationId);
        NominationStatus newStatus;
        try {
            newStatus = NominationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid nomination status: " + status);
        }
        nomination.setStatus(newStatus);
        return toResponse(nominationRepository.save(nomination));
    }

    @Override
    public NominationResponse getById(Long nominationId) {
        return toResponse(getEntity(nominationId));
    }

    @Override
    public List<NominationResponse> getByProgramme(Long programmeId) {
        return nominationRepository.findByProgramme_ProgrammeId(programmeId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NominationResponse> getByOfficer(Long officerId) {
        return nominationRepository.findByOfficer_OfficerId(officerId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NominationResponse> getByDepartment(Long departmentId) {
        return nominationRepository.findByNominatingDepartment_DepartmentId(departmentId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NominationResponse> findDuplicatesForProgramme(Long programmeId) {
        return nominationRepository.findDuplicateNominationsForProgramme(programmeId, ACTIVE_STATUSES)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<NominationResponse> findOtherActiveNominationsForOfficer(Long officerId, Long excludeProgrammeId) {
        return nominationRepository.findOtherActiveNominationsForOfficer(officerId, excludeProgrammeId, ACTIVE_STATUSES)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void withdraw(Long nominationId) {
        Nomination nomination = getEntity(nominationId);
        nomination.setStatus(NominationStatus.WITHDRAWN);
        nominationRepository.save(nomination);
    }

    private Nomination getEntity(Long nominationId) {
        return nominationRepository.findById(nominationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Nomination not found with id: " + nominationId));
    }

    private NominationResponse toResponse(Nomination nomination) {
        return NominationResponse.builder()
                .nominationId(nomination.getNominationId())
                .programmeId(nomination.getProgramme().getProgrammeId())
                .programmeTitle(nomination.getProgramme().getTitle())
                .officerId(nomination.getOfficer().getOfficerId())
                .officerName(nomination.getOfficer().getFullName())
                .nominatingDepartmentId(nomination.getNominatingDepartment().getDepartmentId())
                .nominatingDepartmentName(nomination.getNominatingDepartment().getName())
                .status(nomination.getStatus())
                .nominatedAt(nomination.getNominatedAt())
                .build();
    }
}
