package com.rentequip.backend.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.rentequip.backend.controllers.CompanyController;
import com.rentequip.backend.controllers.EquipmentController;
import com.rentequip.backend.controllers.ReservationController;
import com.rentequip.backend.dtos.response.ReservationResponse;
import com.rentequip.backend.enums.ReservationStatus;
import com.rentequip.backend.services.ReservationStatusPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a reservation in its hypermedia representation. The transition links are offered only when the
 * lifecycle actually allows them, so the client discovers what it can do from the response itself
 * instead of hardcoding the state machine.
 */
@Component
@RequiredArgsConstructor
public class ReservationModelAssembler
        implements RepresentationModelAssembler<ReservationResponse, EntityModel<ReservationResponse>> {

    private final ReservationStatusPolicy statusPolicy;

    @Override
    public EntityModel<ReservationResponse> toModel(ReservationResponse reservation) {
        List<Link> links = new ArrayList<>();
        links.add(linkTo(methodOn(ReservationController.class)
                .findById(reservation.id())).withSelfRel());
        links.add(linkTo(methodOn(EquipmentController.class)
                .findById(reservation.equipmentId())).withRel("equipment"));
        links.add(linkTo(methodOn(CompanyController.class)
                .findById(reservation.renterCompanyId())).withRel("renter"));
        addTransitionLinks(links, reservation);
        return EntityModel.of(reservation, links);
    }

    private void addTransitionLinks(List<Link> links, ReservationResponse reservation) {
        ReservationStatus current = reservation.status();
        if (statusPolicy.canTransitionTo(current, ReservationStatus.CONFIRMED)) {
            links.add(linkTo(methodOn(ReservationController.class)
                    .confirm(reservation.id())).withRel("confirmation"));
        }
        if (statusPolicy.canTransitionTo(current, ReservationStatus.CANCELLED)) {
            links.add(linkTo(methodOn(ReservationController.class)
                    .cancel(reservation.id(), null)).withRel("cancellation"));
        }
    }
}
