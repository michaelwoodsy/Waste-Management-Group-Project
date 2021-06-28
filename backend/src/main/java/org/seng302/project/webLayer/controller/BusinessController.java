package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.serviceLayer.dto.business.AddOrRemoveBusinessAdminDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessDTO;
import org.seng302.project.serviceLayer.dto.business.SearchBusinessDTO;
import org.seng302.project.serviceLayer.service.BusinessService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


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
     * @param requestBody request body containing the id of the user to make an administrator
     * @param appUser the user making the request
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAdministrator(@PathVariable int id, @RequestBody JSONObject requestBody,
                                    @AuthenticationPrincipal AppUserDetails appUser) {
        Integer userId = (Integer) requestBody.getAsNumber("userId");
        var requestDTO = new AddOrRemoveBusinessAdminDTO(userId);
        requestDTO.setBusinessId(id);
        requestDTO.setAppUser(appUser);
        businessService.addAdministrator(requestDTO);

    }

    /**
     * Removes an individual as an administrator for a business
     * Service method handles cases that may result in an error
     *
     * @param id   Id of the business to remove an administrator from
     * @param requestBody request body containing the user id of administrator to remove
     * @param appUser the user making the request
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void removeAdministrator(@PathVariable int id, @RequestBody JSONObject requestBody,
                                    @AuthenticationPrincipal AppUserDetails appUser) {
        Integer userId = (Integer) requestBody.getAsNumber("userId");
        var requestDTO = new AddOrRemoveBusinessAdminDTO(userId);
        requestDTO.setBusinessId(id);
        requestDTO.setAppUser(appUser);
        businessService.removeAdministrator(requestDTO);
    }

    /**
     * Receives a request containing a search query to search businesses by name and retrieves a list
     * of businesses based on the query.
     *
     *
     *
     * @param searchQuery business's name, or part of their name
     * @return 200 response with (potentially empty) list of businesses or
     * 400 if invalid business type provided or
     * 401 if not authenticated.
     */
    @GetMapping("/businesses/search")
    public List<Business> searchBusiness(@RequestParam("searchQuery") String searchQuery,
                                         @RequestParam("businessType") String businessType) {

        if (businessType == null) {
            businessType = "";
        }
        SearchBusinessDTO searchBusinessDTO = new SearchBusinessDTO(searchQuery, businessType);

        return businessService.searchBusiness(searchBusinessDTO);
    }

}
