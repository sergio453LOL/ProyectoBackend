package com.rentequip.backend.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.rentequip.backend.controllers.CompanyController;
import com.rentequip.backend.controllers.EquipmentController;
import com.rentequip.backend.dtos.response.EquipmentResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * Wraps an equipment in its hypermedia representation, pointing at its owner and at the collections
 * that hang from it.
 */
@Component
public class EquipmentModelAssembler
        implements RepresentationModelAssembler<EquipmentResponse, EntityModel<EquipmentResponse>> {

    @Override
    public EntityModel<EquipmentResponse> toModel(EquipmentResponse equipment) {
        var self = linkTo(methodOn(EquipmentController.class).findById(equipment.id()));
        return EntityModel.of(equipment,
                self.withSelfRel(),
                self.slash("reservations").withRel("reservations"),
                self.slash("reviews").withRel("reviews"),
                linkTo(methodOn(CompanyController.class).findById(equipment.ownerCompanyId()))
                        .withRel("owner"));
    }
}
