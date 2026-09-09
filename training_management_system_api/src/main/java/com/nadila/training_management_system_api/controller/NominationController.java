package com.nadila.training_management_system_api.controller;

import com.nadila.training_management_system_api.dto.request.NominationRequest;
import com.nadila.training_management_system_api.enums.ResponseStatus;
import com.nadila.training_management_system_api.response.ApiResponse;
import com.nadila.training_management_system_api.service.NominationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nominations")
@RequiredArgsConstructor
public class NominationController {

    private final NominationService nominationService;

    /**
     * Nominate an officer for a training programme.
     * Returns 409 CONFLICT (via GlobalExceptionHandler) if the officer is
     * already actively nominated for this programme by any department.
     */
    @PostMapping
    public ResponseEntity<ApiResponse> nominate(@Valid @RequestBody NominationRequest request) {
        var created = nominationService.nominate(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(ResponseStatus.SUCCESS, "Nomination submitted successfully", created));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse> updateStatus(@PathVariable Long id, @RequestParam String status) {
        var updated = nominationService.updateStatus(id, status);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Nomination status updated successfully", updated));
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<ApiResponse> withdraw(@PathVariable Long id) {
        nominationService.withdraw(id);
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Nomination withdrawn successfully", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAll() {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Nominations fetched successfully", nominationService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Nomination fetched successfully", nominationService.getById(id)));
    }

    @GetMapping("/programme/{programmeId}")
    public ResponseEntity<ApiResponse> getByProgramme(@PathVariable Long programmeId) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Nominations fetched successfully", nominationService.getByProgramme(programmeId)));
    }

    @GetMapping("/officer/{officerId}")
    public ResponseEntity<ApiResponse> getByOfficer(@PathVariable Long officerId) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Nominations fetched successfully", nominationService.getByOfficer(officerId)));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<ApiResponse> getByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Nominations fetched successfully", nominationService.getByDepartment(departmentId)));
    }

    /**
     * Coordinator safety-net report: officers who ended up with more than one
     * active nomination row for the same programme (e.g. from migrated/legacy data).
     */
    @GetMapping("/programme/{programmeId}/duplicates")
    public ResponseEntity<ApiResponse> findDuplicates(@PathVariable Long programmeId) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Duplicate nominations fetched successfully", nominationService.findDuplicatesForProgramme(programmeId)));
    }

    /**
     * Schedule-clash helper: other active nominations for this officer, excluding
     * the given programme, so the caller can compare dates for overlaps.
     */
    @GetMapping("/officer/{officerId}/clashes")
    public ResponseEntity<ApiResponse> findClashes(@PathVariable Long officerId, @RequestParam Long excludeProgrammeId) {
        return ResponseEntity.ok(new ApiResponse(ResponseStatus.SUCCESS, "Other active nominations fetched successfully", nominationService.findOtherActiveNominationsForOfficer(officerId, excludeProgrammeId)));
    }
}
