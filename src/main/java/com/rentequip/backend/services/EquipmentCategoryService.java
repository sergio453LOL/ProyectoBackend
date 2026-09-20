package com.rentequip.backend.services;

import com.rentequip.backend.dtos.request.EquipmentCategoryCreateRequest;
import com.rentequip.backend.dtos.response.EquipmentCategoryResponse;
import com.rentequip.backend.entities.EquipmentCategory;
import com.rentequip.backend.exceptions.DuplicateResourceException;
import com.rentequip.backend.exceptions.ResourceNotFoundException;
import com.rentequip.backend.mappers.EquipmentCategoryMapper;
import com.rentequip.backend.repositories.EquipmentCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentCategoryService {

    private final EquipmentCategoryRepository categoryRepository;
    private final EquipmentCategoryMapper categoryMapper;

    @Transactional
    public EquipmentCategoryResponse create(EquipmentCategoryCreateRequest request) {
        validateNameIsFree(request.name(), null);
        EquipmentCategory category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Transactional
    public EquipmentCategoryResponse update(Long categoryId, EquipmentCategoryCreateRequest request) {
        EquipmentCategory category = findOrThrow(categoryId);
        validateNameIsFree(request.name(), categoryId);
        category.setName(request.name().trim());
        category.setDescription(request.description());
        return categoryMapper.toResponse(category);
    }

    @Transactional
    public void delete(Long categoryId) {
        categoryRepository.delete(findOrThrow(categoryId));
    }

    @Transactional(readOnly = true)
    public EquipmentCategoryResponse findById(Long categoryId) {
        return categoryMapper.toResponse(findOrThrow(categoryId));
    }

    @Transactional(readOnly = true)
    public List<EquipmentCategoryResponse> findAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    private EquipmentCategory findOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> ResourceNotFoundException.of("EquipmentCategory", categoryId));
    }

    private void validateNameIsFree(String name, Long currentId) {
        boolean taken = currentId == null
                ? categoryRepository.existsByNameIgnoreCase(name)
                : categoryRepository.existsByNameIgnoreCaseAndIdNot(name, currentId);
        if (taken) {
            throw DuplicateResourceException.of("EquipmentCategory", "name", name);
        }
    }
}
