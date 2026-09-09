package com.nadila.training_management_system_api.repository;

import com.nadila.training_management_system_api.entity.TrainingProgramme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrainingProgrammeRepository extends JpaRepository<TrainingProgramme, Long> {

    List<TrainingProgramme> findByTargetDepartments_DepartmentId(Long departmentId);

    List<TrainingProgramme> findByStartDateBetween(LocalDate from, LocalDate to);
}
