package com.nadila.training_management_system_api.repository;

import com.nadila.training_management_system_api.entity.Nomination;
import com.nadila.training_management_system_api.enums.NominationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NominationRepository extends JpaRepository<Nomination, Long> {

    List<Nomination> findByProgramme_ProgrammeId(Long programmeId);

    List<Nomination> findByOfficer_OfficerId(Long officerId);

    List<Nomination> findByNominatingDepartment_DepartmentId(Long departmentId);

    /**
     * Core duplicate-check query: is this officer already actively nominated
     * (PENDING / APPROVED / WAITLISTED) for this programme, regardless of
     * which department nominated them?
     */
    Optional<Nomination> findFirstByProgramme_ProgrammeIdAndOfficer_OfficerIdAndStatusIn(
            Long programmeId, Long officerId, List<NominationStatus> statuses);

    long countByProgramme_ProgrammeIdAndStatus(Long programmeId, NominationStatus status);

    /**
     * Schedule-clash helper: find other active nominations for this officer,
     * excluding the current programme, so callers can check for overlapping dates.
     */
    @Query("""
            SELECT n FROM Nomination n
            WHERE n.officer.officerId = :officerId
            AND n.status IN :activeStatuses
            AND n.programme.programmeId <> :excludeProgrammeId
            """)
    List<Nomination> findOtherActiveNominationsForOfficer(
            @Param("officerId") Long officerId,
            @Param("excludeProgrammeId") Long excludeProgrammeId,
            @Param("activeStatuses") List<NominationStatus> activeStatuses);

    /**
     * Coordinator safety-net report: any officer with more than one active
     * nomination row for the same programme (useful for legacy/migrated data).
     */
    @Query("""
            SELECT n FROM Nomination n
            WHERE n.programme.programmeId = :programmeId
            AND n.status IN :activeStatuses
            AND n.officer.officerId IN (
                SELECT n2.officer.officerId FROM Nomination n2
                WHERE n2.programme.programmeId = :programmeId
                AND n2.status IN :activeStatuses
                GROUP BY n2.officer.officerId
                HAVING COUNT(n2) > 1
            )
            """)
    List<Nomination> findDuplicateNominationsForProgramme(
            @Param("programmeId") Long programmeId,
            @Param("activeStatuses") List<NominationStatus> activeStatuses);
}
