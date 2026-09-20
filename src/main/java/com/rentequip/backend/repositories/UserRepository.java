package com.rentequip.backend.repositories;

import com.rentequip.backend.entities.User;
import com.rentequip.backend.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Fetches the company eagerly because the login flow needs it to build the token claims.
     */
    @Query("SELECT u FROM User u JOIN FETCH u.company WHERE lower(u.email) = lower(:email)")
    Optional<User> findByEmailWithCompany(@Param("email") String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Page<User> findByCompanyId(Long companyId, Pageable pageable);

    Page<User> findByCompanyIdAndRole(Long companyId, UserRole role, Pageable pageable);

    long countByCompanyIdAndRole(Long companyId, UserRole role);
}
