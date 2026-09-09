package com.nadila.training_management_system_api.repository;

import com.nadila.training_management_system_api.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
