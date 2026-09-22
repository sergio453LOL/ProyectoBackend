package com.rentequip.backend.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.rentequip.backend.controllers.CompanyController;
import com.rentequip.backend.dtos.response.CompanyResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

/**
 * Wraps a company in its hypermedia representation. Keeping link building here leaves the controller
 * free of any logic beyond delegating to the service.
 */
@Component
public class CompanyModelAssembler
        implements RepresentationModelAssembler<CompanyResponse, EntityModel<CompanyResponse>> {

    @Override
    public EntityModel<CompanyResponse> toModel(CompanyResponse company) {
        var self = linkTo(methodOn(CompanyController.class).findById(company.id()));
        return EntityModel.of(company,
                self.withSelfRel(),
                self.slash("users").withRel("users"),
                self.slash("equipment").withRel("equipment"));
    }
}
