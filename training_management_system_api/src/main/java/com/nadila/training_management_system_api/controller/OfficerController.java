package com.nadila.training_management_system_api.controller;

import com.nadila.training_management_system_api.dto.request.OfficerRequest;
import com.nadila.training_management_system_api.enums.ResponseStatus;
import com.nadila.training_management_system_api.response.ApiResponse;
import com.nadila.training_management_system_api.service.OfficerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/officers")
@RequiredArgsConstructor
public class OfficerController {

    private final OfficerService officerService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody OfficerRequest request) {
        var created = officerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(ResponseStatus.SUCCESS, "Officer created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody OfficerRequest request) {
        var updated = officerService.update(id, request);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Officer updated successfully", updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Officer fetched successfully", officerService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll(@RequestParam(required = false) Long departmentId) {
        var data = departmentId != null ? officerService.getByDepartment(departmentId) : officerService.getAll();
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Officers fetched successfully", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        officerService.delete(id);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Officer deleted successfully", null));
    }
}
