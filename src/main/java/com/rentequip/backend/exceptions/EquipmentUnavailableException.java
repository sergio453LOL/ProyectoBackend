package com.rentequip.backend.exceptions;

import com.rentequip.backend.enums.EquipmentStatus;
import org.springframework.http.HttpStatus;

/**
 * The equipment exists but its lifecycle status (maintenance, inactive) makes it non rentable.
 */
public class EquipmentUnavailableException extends RentEquipException {

    public EquipmentUnavailableException(String message) {
        super(message, HttpStatus.CONFLICT, "EQUIPMENT_UNAVAILABLE");
    }

    public static EquipmentUnavailableException of(Long equipmentId, EquipmentStatus status) {
        return new EquipmentUnavailableException(
                "Equipment %d cannot be reserved while its status is %s".formatted(equipmentId, status));
    }
}
