package com.nadila.training_management_system_api.repository;

import com.nadila.training_management_system_api.entity.Officer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfficerRepository extends JpaRepository<Officer, Long> {
    Optional<Officer> findByNic(String nic);
    boolean existsByNic(String nic);
    List<Officer> findByDepartment_DepartmentId(Long departmentId);
}
