package com.nadila.training_management_system_api.controller;

import com.nadila.training_management_system_api.dto.request.TrainerRequest;
import com.nadila.training_management_system_api.enums.ResponseStatus;
import com.nadila.training_management_system_api.response.ApiResponse;
import com.nadila.training_management_system_api.service.TrainerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody TrainerRequest request) {
        var created = trainerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(ResponseStatus.SUCCESS, "Trainer created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody TrainerRequest request) {
        var updated = trainerService.update(id, request);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Trainer updated successfully", updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Trainer fetched successfully", trainerService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Trainers fetched successfully", trainerService.getAll()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        trainerService.delete(id);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Trainer deleted successfully", null));
    }
}
