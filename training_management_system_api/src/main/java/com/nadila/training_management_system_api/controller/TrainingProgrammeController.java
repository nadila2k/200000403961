package com.nadila.training_management_system_api.controller;

import com.nadila.training_management_system_api.dto.request.TrainingProgrammeRequest;
import com.nadila.training_management_system_api.enums.ResponseStatus;
import com.nadila.training_management_system_api.response.ApiResponse;
import com.nadila.training_management_system_api.service.TrainingProgrammeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/programmes")
@RequiredArgsConstructor
public class TrainingProgrammeController {

    private final TrainingProgrammeService programmeService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody TrainingProgrammeRequest request) {
        var created = programmeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(ResponseStatus.SUCCESS, "Training programme created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody TrainingProgrammeRequest request) {
        var updated = programmeService.update(id, request);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Training programme updated successfully", updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Training programme fetched successfully", programmeService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Training programmes fetched successfully", programmeService.getAll()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        programmeService.delete(id);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Training programme deleted successfully", null));
    }
}
