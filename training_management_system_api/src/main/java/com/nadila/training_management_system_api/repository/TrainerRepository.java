package com.nadila.training_management_system_api.repository;

import com.nadila.training_management_system_api.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
}
