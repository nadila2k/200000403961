package com.nadila.training_management_system_api.controller;

import com.nadila.training_management_system_api.dto.request.VenueRequest;
import com.nadila.training_management_system_api.enums.ResponseStatus;
import com.nadila.training_management_system_api.response.ApiResponse;
import com.nadila.training_management_system_api.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody VenueRequest request) {
        var created = venueService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(ResponseStatus.SUCCESS, "Venue created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        var updated = venueService.update(id, request);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Venue updated successfully", updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Venue fetched successfully", venueService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Venues fetched successfully", venueService.getAll()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Venue deleted successfully", null));
    }
}
