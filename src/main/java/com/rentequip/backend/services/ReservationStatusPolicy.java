package com.rentequip.backend.services;

import com.rentequip.backend.entities.Reservation;
import com.rentequip.backend.enums.ReservationStatus;
import com.rentequip.backend.exceptions.ForbiddenOperationException;
import com.rentequip.backend.exceptions.InvalidStatusTransitionException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Owns the reservation lifecycle rules: which transitions exist and who is allowed to trigger them.
 * Keeping them here leaves ReservationService responsible only for orchestration and persistence.
 */
@Component
public class ReservationStatusPolicy {

    private static final Map<ReservationStatus, Set<ReservationStatus>> ALLOWED_TRANSITIONS =
            new EnumMap<>(ReservationStatus.class);

    private static final Set<ReservationStatus> OWNER_ONLY = Set.of(
            ReservationStatus.CONFIRMED,
            ReservationStatus.REJECTED,
            ReservationStatus.IN_PROGRESS,
            ReservationStatus.COMPLETED
    );

    static {
        ALLOWED_TRANSITIONS.put(ReservationStatus.PENDING, Set.of(
                ReservationStatus.CONFIRMED, ReservationStatus.REJECTED, ReservationStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(ReservationStatus.CONFIRMED, Set.of(
                ReservationStatus.IN_PROGRESS, ReservationStatus.CANCELLED));
        ALLOWED_TRANSITIONS.put(ReservationStatus.IN_PROGRESS, Set.of(ReservationStatus.COMPLETED));
        ALLOWED_TRANSITIONS.put(ReservationStatus.COMPLETED, Set.of());
        ALLOWED_TRANSITIONS.put(ReservationStatus.CANCELLED, Set.of());
        ALLOWED_TRANSITIONS.put(ReservationStatus.REJECTED, Set.of());
    }

    public void validateTransition(Reservation reservation, ReservationStatus target, Long actingCompanyId) {
        validateTargetIsReachable(reservation.getStatus(), target);
        validateActorIsAllowed(reservation, target, actingCompanyId);
    }

    /**
     * Whether the transition exists at all, ignoring who is asking. Callers that only need to know if an
     * action is offered (hypermedia links, UI affordances) use this instead of catching the exception.
     */
    public boolean canTransitionTo(ReservationStatus current, ReservationStatus target) {
        return current != target && ALLOWED_TRANSITIONS.getOrDefault(current, Set.of()).contains(target);
    }

    /**
     * True when the new status releases the calendar, which is what lets the equipment go back to
     * AVAILABLE and lets other companies book the same window.
     */
    public boolean releasesCalendar(ReservationStatus status) {
        return status == ReservationStatus.CANCELLED
                || status == ReservationStatus.REJECTED
                || status == ReservationStatus.COMPLETED;
    }

    private void validateTargetIsReachable(ReservationStatus current, ReservationStatus target) {
        if (current == target) {
            throw new InvalidStatusTransitionException("The reservation is already " + target);
        }
        if (!canTransitionTo(current, target)) {
            throw InvalidStatusTransitionException.of(current, target);
        }
    }

    private void validateActorIsAllowed(Reservation reservation, ReservationStatus target, Long actingCompanyId) {
        Long ownerId = reservation.getEquipment().getOwner().getId();
        Long renterId = reservation.getRenter().getId();

        if (OWNER_ONLY.contains(target) && !ownerId.equals(actingCompanyId)) {
            throw new ForbiddenOperationException("Only the equipment owner can move the reservation to " + target);
        }
        if (target == ReservationStatus.CANCELLED
                && !renterId.equals(actingCompanyId) && !ownerId.equals(actingCompanyId)) {
            throw new ForbiddenOperationException("Only the renter or the owner can cancel this reservation");
        }
    }
}
