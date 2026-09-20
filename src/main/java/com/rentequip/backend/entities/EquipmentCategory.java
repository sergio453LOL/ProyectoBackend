package com.rentequip.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equipment_categories")
@Getter
@Setter
@NoArgsConstructor
public class EquipmentCategory extends BaseEntity {

    @NotBlank
    @Size(min = 2, max = 80)
    @Column(nullable = false, unique = true, length = 80)
    private String name;

    @Size(max = 255)
    @Column(length = 255)
    private String description;
}
