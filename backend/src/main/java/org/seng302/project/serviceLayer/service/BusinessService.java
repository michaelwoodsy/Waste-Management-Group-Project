package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.AddBusinessDTO;
import org.seng302.project.serviceLayer.exceptions.InvalidDateException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.register.UserUnderageException;
import org.seng302.project.serviceLayer.util.DateArithmetic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class BusinessService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessService.class.getName());
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                              AddressRepository addressRepository,
                              UserRepository userRepository) {
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new business account.
     *
     * @param requestDTO DTO with fields for Business to be created
     */
    public void createBusiness(AddBusinessDTO requestDTO) {
        logger.info("Request to create business");

        try {

            var userEmail = "";
            String businessName = requestDTO.getName();
            String description = requestDTO.getDescription();
            var address = requestDTO.getAddress();
            String businessType = requestDTO.getBusinessType();
            Integer primaryAdministratorId = requestDTO.getPrimaryAdministratorId();


            var newBusiness = new Business(businessName, description, address, businessType, primaryAdministratorId);

            //If the primary administrator id is not an id of a user
            if (userRepository.findById(primaryAdministratorId).isEmpty()) {
                var exception = new NoUserExistsException(primaryAdministratorId);
                logger.error(exception.getMessage());
                throw exception;
            } else {
                Optional<User> currUser = userRepository.findById(primaryAdministratorId);
                currUser.ifPresent(newBusiness::addAdministrator);
                if(currUser.isPresent()) userEmail = currUser.get().getEmail();
            }

            var currentUser = userRepository.findByEmail(userEmail).get(0);

            //If the current user is less than 16 years old
            Date dateOfBirthDate;
            var currentDate = new Date();
            try {
                dateOfBirthDate = new SimpleDateFormat("yyyy-MM-dd").parse(currentUser.getDateOfBirth());
            } catch (ParseException parseException) {
                var invalidDateException = new InvalidDateException();
                logger.warn(invalidDateException.getMessage());
                throw invalidDateException;
            } catch (Exception exception) {
                logger.error(String.format("Unexpected error while parsing date: %s", exception.getMessage()));
                throw exception;
            }

            if (DateArithmetic.getDiffYears(dateOfBirthDate, currentDate) < 16) {
                var underageException = new UserUnderageException("a business", 16);
                logger.warn(underageException.getMessage());
                throw underageException;
            }

            addressRepository.save(address);
            businessRepository.save(newBusiness);
            logger.info("Successful creation of business {}", newBusiness.getId());
        } catch (NoUserExistsException handledException) {
            throw handledException;
        } catch (Exception unexpectedException) {
            logger.error(String.format("Unexpected error while creating business: %s", unexpectedException.getMessage()));
            throw unexpectedException;
        }
    }
}
