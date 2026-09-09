package com.nadila.training_management_system_api.service.impl;

import com.nadila.training_management_system_api.dto.request.VenueRequest;
import com.nadila.training_management_system_api.dto.response.VenueResponse;
import com.nadila.training_management_system_api.entity.Venue;
import com.nadila.training_management_system_api.exception.ResourceNotFoundException;
import com.nadila.training_management_system_api.repository.VenueRepository;
import com.nadila.training_management_system_api.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public VenueResponse create(VenueRequest request) {
        Venue venue = modelMapper.map(request, Venue.class);
        return toResponse(venueRepository.save(venue));
    }

    @Override
    @Transactional
    public VenueResponse update(Long venueId, VenueRequest request) {
        Venue venue = getEntity(venueId);
        venue.setName(request.getName());
        venue.setLocation(request.getLocation());
        venue.setCapacity(request.getCapacity());
        return toResponse(venueRepository.save(venue));
    }

    @Override
    public VenueResponse getById(Long venueId) {
        return toResponse(getEntity(venueId));
    }

    @Override
    public List<VenueResponse> getAll() {
        return venueRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void delete(Long venueId) {
        venueRepository.delete(getEntity(venueId));
    }

    private Venue getEntity(Long venueId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + venueId));
    }

    private VenueResponse toResponse(Venue venue) {
        return modelMapper.map(venue, VenueResponse.class);
    }
}
