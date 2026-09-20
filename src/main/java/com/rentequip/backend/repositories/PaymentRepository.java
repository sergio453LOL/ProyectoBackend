package com.rentequip.backend.repositories;

import com.rentequip.backend.entities.Payment;
import com.rentequip.backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByReservationId(Long reservationId);

    List<Payment> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);

    Optional<Payment> findByProviderReference(String providerReference);
}
