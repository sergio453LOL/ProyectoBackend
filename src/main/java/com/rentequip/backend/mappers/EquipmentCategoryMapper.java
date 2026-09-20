package com.rentequip.backend.mappers;

import com.rentequip.backend.dtos.request.EquipmentCategoryCreateRequest;
import com.rentequip.backend.dtos.response.EquipmentCategoryResponse;
import com.rentequip.backend.entities.EquipmentCategory;
import org.springframework.stereotype.Component;

@Component
public class EquipmentCategoryMapper {

    public EquipmentCategory toEntity(EquipmentCategoryCreateRequest request) {
        EquipmentCategory category = new EquipmentCategory();
        category.setName(request.name().trim());
        category.setDescription(request.description());
        return category;
    }

    public EquipmentCategoryResponse toResponse(EquipmentCategory category) {
        return new EquipmentCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
