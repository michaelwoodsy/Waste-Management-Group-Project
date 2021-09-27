package org.seng302.project.web_layer.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.repository_layer.model.BusinessNotification;
import org.seng302.project.repository_layer.model.enums.BusinessType;
import org.seng302.project.service_layer.dto.business.GetBusinessDTO;
import org.seng302.project.service_layer.dto.business.PostBusinessDTO;
import org.seng302.project.service_layer.dto.business.PutBusinessAdminDTO;
import org.seng302.project.service_layer.exceptions.BadRequestException;
import org.seng302.project.service_layer.service.BusinessService;
import org.seng302.project.service_layer.service.UserService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(BusinessController.class.getName());


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
    public List<Object> searchBusiness(@RequestParam("searchQuery") String searchQuery,
                                         @RequestParam(name = "businessType", required = false)
                                                 String businessTypeParam, @RequestParam("pageNumber") Integer pageNumber,
                                         @RequestParam("sortBy") String sortBy){

        BusinessType businessType = null;
        if (businessTypeParam != null) {
            if (BusinessType.checkType(businessTypeParam)) {
                businessType = BusinessType.getType(businessTypeParam);
            } else {
                throw new BadRequestException("Invalid business type provided");
            }
        }

        logger.info("Request to search businesses with searchQuery: {} and businessType: {}",
                searchQuery, businessType);
        try{
            return businessService.searchBusiness(searchQuery, businessType, pageNumber, sortBy);
        } catch (BadRequestException badRequestException) {
            logger.error(badRequestException.getMessage());
            throw badRequestException;
        } catch (Exception exception) {
            logger.error(String.format("Unexpected error while searching businesses: %s", exception.getMessage()));
            throw exception;
        }
    }

    /**
     * Gets all the notifications for a business
     *
     * @param businessId the id of the business to get notifications for
     * @param appUser the user making the request
     */
    @GetMapping("/businesses/{businessId}/notifications")
    public List<BusinessNotification> getBusinessNotifications(@PathVariable Integer businessId,
                                                               @AuthenticationPrincipal AppUserDetails appUser) {
        return businessService.getBusinessNotifications(businessId, appUser);
    }

    /**
     * Deletes a notification from a business
     *
     * @param businessId the id of the business to delete the notification for
     * @param notificationId the id of the notification to delete
     * @param appUser the user making the request
     */
    @DeleteMapping("/businesses/{businessId}/notifications/{notificationId}")
    public void deleteBusinessNotification(@PathVariable Integer businessId, @PathVariable Integer notificationId,
                                           @AuthenticationPrincipal AppUserDetails appUser) {
        businessService.deleteBusinessNotification(businessId, notificationId, appUser);

    }

    /**
     * Marks a business' notification as read/unread
     *
     * @param businessId the id of the business to read/unread the notification for
     * @param notificationId the id of the notification to mark as read/unread
     * @param requestBody request body containing whether to mark the notification as read or not read
     * @param appUser the user making the request
     */
    @PatchMapping("/businesses/{businessId}/notifications/{notificationId}/read")
    public void readBusinessNotification(@PathVariable Integer businessId, @PathVariable Integer notificationId,
                                         @RequestBody JSONObject requestBody,
                                           @AuthenticationPrincipal AppUserDetails appUser) {

        try {
            Boolean read = (Boolean) requestBody.get("read");
            businessService.readBusinessNotification(businessId, notificationId, read, appUser);
        } catch (ClassCastException castException) {
            String message = "Request body must contain a 'read' field with a boolean value";
            logger.warn(message);
            throw new BadRequestException(message);
        }
    }

}
