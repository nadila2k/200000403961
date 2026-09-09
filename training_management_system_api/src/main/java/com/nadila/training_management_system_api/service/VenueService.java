package com.nadila.training_management_system_api.service;

import com.nadila.training_management_system_api.dto.request.VenueRequest;
import com.nadila.training_management_system_api.dto.response.VenueResponse;

import java.util.List;

public interface VenueService {
    VenueResponse create(VenueRequest request);
    VenueResponse update(Long venueId, VenueRequest request);
    VenueResponse getById(Long venueId);
    List<VenueResponse> getAll();
    void delete(Long venueId);
}
