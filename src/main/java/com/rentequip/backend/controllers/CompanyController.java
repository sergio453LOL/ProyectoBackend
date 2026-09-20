package com.rentequip.backend.controllers;

import com.rentequip.backend.dtos.request.CompanyCreateRequest;
import com.rentequip.backend.dtos.request.CompanyUpdateRequest;
import com.rentequip.backend.dtos.response.CompanyResponse;
import com.rentequip.backend.dtos.response.EquipmentSummaryResponse;
import com.rentequip.backend.dtos.response.PageResponse;
import com.rentequip.backend.dtos.response.UserResponse;
import com.rentequip.backend.enums.UserRole;
import com.rentequip.backend.services.CompanyService;
import com.rentequip.backend.services.EquipmentService;
import com.rentequip.backend.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Validated
public class CompanyController {

    private static final String ACTING_COMPANY_HEADER = "X-Company-Id";

    private final CompanyService companyService;
    private final UserService userService;
    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody CompanyCreateRequest request,
                                                   UriComponentsBuilder uriBuilder) {
        CompanyResponse created = companyService.create(request);
        URI location = uriBuilder.path("/api/v1/companies/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.findById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<CompanyResponse>> findAll(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(companyService.findAll(city, pageable));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CompanyUpdateRequest request,
            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId) {
        return ResponseEntity.ok(companyService.update(id, request, actingCompanyId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id,
                                            @RequestHeader(ACTING_COMPANY_HEADER) @NotNull Long actingCompanyId) {
        companyService.deactivate(id, actingCompanyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<PageResponse<UserResponse>> findUsers(
            @PathVariable Long id,
            @RequestParam(required = false) UserRole role,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "lastName"));
        return ResponseEntity.ok(userService.findByCompany(id, role, pageable));
    }

    @GetMapping("/{id}/equipment")
    public ResponseEntity<PageResponse<EquipmentSummaryResponse>> findEquipment(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(equipmentService.findByOwner(id, pageable));
    }
}
