package org.seng302.project.webLayer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.types.BusinessType;
import org.seng302.project.serviceLayer.dto.business.GetBusinessDTO;
import org.seng302.project.serviceLayer.dto.business.PostBusinessDTO;
import org.seng302.project.serviceLayer.dto.business.PutBusinessAdminDTO;
import org.seng302.project.serviceLayer.exceptions.BadRequestException;
import org.seng302.project.serviceLayer.service.BusinessService;
import org.seng302.project.serviceLayer.service.UserService;
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
    private final UserService userService;


    @Autowired
    public BusinessController(BusinessService businessService, UserService userService) {
        this.businessService = businessService;
        this.userService = userService;
    }

    /**
     * Creates a new business account.
     *
     * @param requestDTO DTO with fields for Business to be created
     */
    @PostMapping("/businesses")
    @ResponseStatus(HttpStatus.CREATED)
    public JSONObject createBusiness(@Valid @RequestBody PostBusinessDTO requestDTO, @AuthenticationPrincipal AppUserDetails appUser) {
        userService.checkForbidden(requestDTO.getPrimaryAdministratorId(), appUser);
        Integer businessId = businessService.createBusiness(requestDTO);
        JSONObject response = new JSONObject();
        response.put("businessId", businessId);
        return response;
    }

    /**
     * Retrieve a specific business account.
     * Service method handles cases that may result in an error
     *
     * @param businessId ID of business to get information from.
     * @return Response back to client encompassing status code, headers, and body.
     */
    @GetMapping("/businesses/{businessId}")
    public GetBusinessDTO getBusiness(@PathVariable Integer businessId) {
        return businessService.getBusiness(businessId);
    }

    /**
     * Edits a business with new details provided
     *
     * @param businessId ID of the business to edit
     * @param requestDTO DTO containing new business information
     * @param appUser    currently logged in user
     */
    @PutMapping("/businesses/{businessId}")
    public void editBusiness(@PathVariable Integer businessId,
                             @Valid @RequestBody PostBusinessDTO requestDTO,
                             @AuthenticationPrincipal AppUserDetails appUser,
                             @RequestParam(required = false) Boolean updateProductCurrency) {
        businessService.editBusiness(requestDTO, businessId, appUser, updateProductCurrency);
    }

    /**
     * Adds an individual as an administrator for a business
     * Service method handles cases that may result in an error
     *
     * @param id          Id of the business to add an administrator to
     * @param requestBody request body containing the id of the user to make an administrator
     * @param appUser     the user making the request
     */
    @PutMapping("/businesses/{id}/makeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAdministrator(@PathVariable int id, @RequestBody JSONObject requestBody,
                                    @AuthenticationPrincipal AppUserDetails appUser) {
        Integer userId = (Integer) requestBody.getAsNumber("userId");
        var requestDTO = new PutBusinessAdminDTO(userId);
        requestDTO.setBusinessId(id);
        requestDTO.setAppUser(appUser);
        businessService.addAdministrator(requestDTO);

    }

    /**
     * Removes an individual as an administrator for a business
     * Service method handles cases that may result in an error
     *
     * @param id          Id of the business to remove an administrator from
     * @param requestBody request body containing the user id of administrator to remove
     * @param appUser     the user making the request
     */
    @PutMapping("/businesses/{id}/removeAdministrator")
    @ResponseStatus(HttpStatus.OK)
    public void removeAdministrator(@PathVariable int id, @RequestBody JSONObject requestBody,
                                    @AuthenticationPrincipal AppUserDetails appUser) {
        Integer userId = (Integer) requestBody.getAsNumber("userId");
        var requestDTO = new PutBusinessAdminDTO(userId);
        requestDTO.setBusinessId(id);
        requestDTO.setAppUser(appUser);
        businessService.removeAdministrator(requestDTO);
    }

    /**
     * Receives a request containing a search query to search businesses by name and retrieves a list
     * of businesses based on the query.
     *
     * @param searchQuery business's name, or part of their name
     * @return 200 response with (potentially empty) list of businesses or
     * 400 if invalid business type provided or
     * 401 if not authenticated.
     */
    @GetMapping("/businesses/search")
    public List<Business> searchBusiness(@RequestParam("searchQuery") String searchQuery,
                                         @RequestParam(name = "businessType", required = false)
                                                 String businessTypeParam) {

        BusinessType businessType = null;
        if (businessTypeParam != null) {
            if (BusinessType.checkType(businessTypeParam)) {
                businessType = BusinessType.getType(businessTypeParam);
            } else {
                throw new BadRequestException("Invalid business type provided");
            }
        }

        return businessService.searchBusiness(searchQuery, businessType);
    }

}
