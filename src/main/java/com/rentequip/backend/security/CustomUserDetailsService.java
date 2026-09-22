package com.rentequip.backend.security;

import com.rentequip.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads the principal by email. Uses the fetch-join query so the company is already loaded when the
 * token claims are built, instead of tripping over a lazy proxy outside the transaction.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CompanyUserDetails loadUserByUsername(String email) {
        return userRepository.findByEmailWithCompany(email)
                .map(CompanyUserDetails::from)
                .orElseThrow(() -> new UsernameNotFoundException("No user registered with email " + email));
    }
}
