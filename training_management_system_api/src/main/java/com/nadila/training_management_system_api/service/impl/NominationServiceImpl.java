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
    private final com.nadila.training_management_system_api.service.EligibilityService eligibilityService;

    @Override
    @Transactional
    public NominationResponse nominate(NominationRequest request) {

        TrainingProgramme programme = programmeRepository.findByIdForUpdate(request.getProgrammeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Training programme not found with id: " + request.getProgrammeId()));

        Officer officer = officerRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Officer not found with id: " + request.getOfficerId()));

        Department nominatingDepartment = departmentRepository.findById(request.getNominatingDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + request.getNominatingDepartmentId()));

        // Validate Officer Eligibility
        var eligibilityCheck = eligibilityService.evaluateEligibility(programme, officer);
        if (!eligibilityCheck.isEligible()) {
            String msg = String.format("Officer '%s' is not eligible for programme '%s': %s",
                    officer.getFullName(), programme.getTitle(), String.join("; ", eligibilityCheck.getFailureReasons()));
            throw new com.nadila.training_management_system_api.exception.IneligibleOfficerException(msg, eligibilityCheck.getFailureReasons());
        }


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


        long approvedCount = nominationRepository.countByProgramme_ProgrammeIdAndStatus(
                programme.getProgrammeId(), NominationStatus.APPROVED);

        NominationStatus initialStatus = approvedCount < programme.getMaxParticipants()
                ? NominationStatus.APPROVED
                : NominationStatus.WAITLISTED;

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
        NominationStatus oldStatus = nomination.getStatus();
        NominationStatus newStatus;
        try {
            newStatus = NominationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid nomination status: " + status);
        }


        if (newStatus == NominationStatus.APPROVED && oldStatus != NominationStatus.APPROVED) {

            TrainingProgramme lockedProgramme = programmeRepository
                    .findByIdForUpdate(nomination.getProgramme().getProgrammeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Training programme not found with id: " + nomination.getProgramme().getProgrammeId()));

            long approvedCount = nominationRepository.countByProgramme_ProgrammeIdAndStatus(
                    lockedProgramme.getProgrammeId(), NominationStatus.APPROVED);

            if (approvedCount >= lockedProgramme.getMaxParticipants()) {
                throw new IllegalStateException(String.format(
                        "Cannot approve nomination %d: '%s' is already at full capacity (%d/%d).",
                        nominationId, lockedProgramme.getTitle(), approvedCount, lockedProgramme.getMaxParticipants()));
            }
        }

        nomination.setStatus(newStatus);
        Nomination saved = nominationRepository.save(nomination);


        if (oldStatus == NominationStatus.APPROVED && newStatus != NominationStatus.APPROVED) {
            promoteNextWaitlistedNominationIfSeatAvailable(nomination.getProgramme().getProgrammeId());
        }

        return toResponse(saved);
    }

    @Override
    public NominationResponse getById(Long nominationId) {
        return toResponse(getEntity(nominationId));
    }

    @Override
    public List<NominationResponse> getAll() {
        return nominationRepository.findAll()
                .stream().map(this::toResponse).toList();
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
        NominationStatus oldStatus = nomination.getStatus();
        nomination.setStatus(NominationStatus.WITHDRAWN);
        nominationRepository.save(nomination);

        // If a confirmed (APPROVED) participant cancels/withdraws, promote earliest waitlisted nomination (FIFO)
        if (oldStatus == NominationStatus.APPROVED) {
            promoteNextWaitlistedNominationIfSeatAvailable(nomination.getProgramme().getProgrammeId());
        }
    }


    private void promoteNextWaitlistedNominationIfSeatAvailable(Long programmeId) {

        TrainingProgramme programme = programmeRepository.findByIdForUpdate(programmeId).orElse(null);
        if (programme == null) return;

        long approvedCount = nominationRepository.countByProgramme_ProgrammeIdAndStatus(
                programmeId, NominationStatus.APPROVED);

        if (approvedCount < programme.getMaxParticipants()) {
            Optional<Nomination> nextWaitlisted = nominationRepository
                    .findFirstByProgramme_ProgrammeIdAndStatusOrderByNominatedAtAsc(
                            programmeId, NominationStatus.WAITLISTED);

            if (nextWaitlisted.isPresent()) {
                Nomination promoted = nextWaitlisted.get();
                promoted.setStatus(NominationStatus.APPROVED);
                nominationRepository.save(promoted);
            }
        }
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