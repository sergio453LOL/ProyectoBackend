package com.rentequip.backend.repositories;

import com.rentequip.backend.entities.EquipmentCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, Long> {

    Optional<EquipmentCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<EquipmentCategory> findAllByIdIn(Set<Long> ids);

    List<EquipmentCategory> findAllByOrderByNameAsc();
}
