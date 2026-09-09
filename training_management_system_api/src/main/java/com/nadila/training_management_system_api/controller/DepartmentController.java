package com.nadila.training_management_system_api.controller;

import com.nadila.training_management_system_api.dto.request.DepartmentRequest;
import com.nadila.training_management_system_api.enums.ResponseStatus;
import com.nadila.training_management_system_api.response.ApiResponse;
import com.nadila.training_management_system_api.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody DepartmentRequest request) {
        var created = departmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(ResponseStatus.SUCCESS, "Department created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        var updated = departmentService.update(id, request);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Department updated successfully", updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Department fetched successfully", departmentService.getById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Departments fetched successfully", departmentService.getAll()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Department deleted successfully", null));
    }
}
