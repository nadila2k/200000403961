package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.NominationRequest;
import com.nadila.training_management_system_api.dto.response.NominationResponse;

import java.util.List;

public interface NominationService {

    /**
     * Nominate an officer for a programme. Enforces:
     *  - one active (PENDING/APPROVED/WAITLISTED) nomination per officer per programme,
     *    regardless of which department submits it (duplicate prevention);
     *  - automatic waitlisting once approved count reaches programme capacity.
     */
    NominationResponse nominate(NominationRequest request);

    NominationResponse updateStatus(Long nominationId, String status);

    NominationResponse getById(Long nominationId);

    List<NominationResponse> getByProgramme(Long programmeId);

    List<NominationResponse> getByOfficer(Long officerId);

    List<NominationResponse> getByDepartment(Long departmentId);

    /** Coordinator safety-net: officers with more than one active nomination row for a programme. */
    List<NominationResponse> findDuplicatesForProgramme(Long programmeId);

    /** Schedule-clash check: this officer's other active nominations, for the caller to compare dates against. */
    List<NominationResponse> findOtherActiveNominationsForOfficer(Long officerId, Long excludeProgrammeId);

    void withdraw(Long nominationId);
}
