package com.rentequip.backend.repositories;

import com.rentequip.backend.entities.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByTaxId(String taxId);

    Optional<Company> findByEmailIgnoreCase(String email);

    boolean existsByTaxId(String taxId);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Page<Company> findByActiveTrue(Pageable pageable);

    Page<Company> findByCityIgnoreCaseAndActiveTrue(String city, Pageable pageable);
}
