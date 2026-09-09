package com.nadila.training_management_system_api.repository;

import com.nadila.training_management_system_api.entity.TrainingProgramme;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingProgrammeRepository extends JpaRepository<TrainingProgramme, Long> {

    List<TrainingProgramme> findByTargetDepartments_DepartmentId(Long departmentId);

    List<TrainingProgramme> findByStartDateBetween(LocalDate from, LocalDate to);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from TrainingProgramme p where p.programmeId = :programmeId")
    Optional<TrainingProgramme> findByIdForUpdate(@Param("programmeId") Long programmeId);
}
