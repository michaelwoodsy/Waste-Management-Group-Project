package org.seng302.project.webLayer.controller;

import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.serviceLayer.dto.AddOrRemoveBusinessAdminDTO;
import org.seng302.project.serviceLayer.dto.AddBusinessDTO;
import org.seng302.project.serviceLayer.service.BusinessService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * REST controller for handling requests to do with businesses.
 */
@RestController
public class BusinessController {

    private final BusinessService businessService;


    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }


    /**
     * Creates a new business account.
     *
     * @param requestDTO DTO with fields for Business to be created
     */
    @PostMapping("/businesses")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBusiness(@Valid @RequestBody AddBusinessDTO requestDTO) {
        businessService.createBusiness(requestDTO);
    }

    /**
     * Retrieve a specific business account.
     * Service method handles cases that may result in an error
     *
     * @param id ID of business to get information from.
     * @return Response back to client encompassing status code, headers, and body.
     */
    @GetMapping("/businesses/{id}")
    public Business getBusiness(@PathVariable int id) {
        return businessService.getBusiness(id);
    }

    /**
     * Adds an individual as an administrator for a business
     * Service method handles cases that may result in an error
     *
     * @param id   Id of the business to add an administrator to
     * @param requestDTO request body containing the id of the user to make an administrator
     * @param appUser the user making the request
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAdministrator(@PathVariable int id, @Valid @RequestBody AddOrRemoveBusinessAdminDTO requestDTO,
                                    @AuthenticationPrincipal AppUserDetails appUser) {
        //TODO: Currently only returns 400 responses because:
        // [org.springframework.http.converter.HttpMessageNotReadableException:
        // JSON parse error: Cannot construct instance of `org.seng302.project.serviceLayer.dto.AddOrRemoveBusinessAdminDTO`
        // (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator);
        // nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of
        // `org.seng302.project.serviceLayer.dto.AddOrRemoveBusinessAdminDTO` (although at least one Creator exists):
        // cannot deserialize from Object value (no delegate- or property-based Creator)

        requestDTO.setBusinessId(id);
        requestDTO.setAppUser(appUser);
        businessService.addAdministrator(requestDTO);

    }

    /**
     * Removes an individual as an administrator for a business
     * Service method handles cases that may result in an error
     *
     * @param id   Id of the business to remove an administrator from
     * @param requestDTO request body containing the user id of administrator to remove
     * @param appUser the user making the request
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void removeAdministrator(@PathVariable int id, @Valid @RequestBody AddOrRemoveBusinessAdminDTO requestDTO,
                                    @AuthenticationPrincipal AppUserDetails appUser) {

        //TODO: Currently only returns 400 responses because:
        // [org.springframework.http.converter.HttpMessageNotReadableException:
        // JSON parse error: Cannot construct instance of `org.seng302.project.serviceLayer.dto.AddOrRemoveBusinessAdminDTO`
        // (although at least one Creator exists): cannot deserialize from Object value (no delegate- or property-based Creator);
        // nested exception is com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of
        // `org.seng302.project.serviceLayer.dto.AddOrRemoveBusinessAdminDTO` (although at least one Creator exists):
        // cannot deserialize from Object value (no delegate- or property-based Creator)

        requestDTO.setBusinessId(id);
        requestDTO.setAppUser(appUser);
        businessService.removeAdministrator(requestDTO);
    }

}
