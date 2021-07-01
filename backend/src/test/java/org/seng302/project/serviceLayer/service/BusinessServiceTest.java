package org.seng302.project.serviceLayer.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.model.types.BusinessType;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.repositoryLayer.specification.BusinessSpecifications;
import org.seng302.project.serviceLayer.dto.business.AddBusinessDTO;
import org.seng302.project.serviceLayer.dto.business.AddOrRemoveBusinessAdminDTO;
import org.seng302.project.serviceLayer.dto.business.SearchBusinessDTO;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenPrimaryAdministratorActionException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.serviceLayer.exceptions.register.UserUnderageException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;


@SpringBootTest
class BusinessServiceTest {

    @Autowired
    private BusinessService businessService;

    @MockBean
    private AddressRepository addressRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private BusinessRepository businessRepository;

    private User testPrimaryAdmin;
    private User testUser;
    private Business testBusiness;
    private Address businessAddress;

    @BeforeEach
    public void setup() {

        //Mock a test user to be used as business primary admin
        testPrimaryAdmin = new User("Jim", "Smith", "", "", "",
                "jimsmith@gmail.com", "1999-04-27", "",
                null, "1337-H%nt3r2");

        testPrimaryAdmin.setId(1);
        testPrimaryAdmin.setPassword(passwordEncoder.encode(testPrimaryAdmin.getPassword()));
        given(userRepository.findByEmail("jimsmith@gmail.com")).willReturn(List.of(testPrimaryAdmin));
        given(userRepository.findById(1)).willReturn(Optional.of(testPrimaryAdmin));

        //Mock a different test user
        testUser = new User("Dave", "Sims", "", "", "",
                "DaveSims@gmail.com", "1998-04-27", "",
                null, "1337-H%nt3r2");

        testUser.setId(2);
        testUser.setPassword(passwordEncoder.encode(testUser.getPassword()));
        given(userRepository.findByEmail("DaveSims@gmail.com")).willReturn(List.of(testUser));
        given(userRepository.findById(2)).willReturn(Optional.of(testUser));

        //Mock a test business
        //Spy on this business so we can check when methods on the business object are called
        testBusiness = Mockito.spy(new Business("Lumbridge General Store", "A one-stop shop for all your adventuring needs",
                null, "Accommodation and Food Services", 1));
        testBusiness.setId(1);
        given(businessRepository.findByName("Lumbridge General Store")).willReturn(List.of(testBusiness));
        given(businessRepository.findById(1)).willReturn(Optional.of(testBusiness));

        businessAddress = new Address("", "", "", "", "New Zealand", "");

    }

    /**
     * Creates the test business from the API
     * Checks that the new business and its address are saved
     */
    @Test
    void createBusiness_success() {

        AddBusinessDTO requestDTO = new AddBusinessDTO(
                "Lumbridge General Store",
                "A one-stop shop for all your adventuring needs",
                businessAddress,
                "Accommodation and Food Services",
                testPrimaryAdmin.getId()
        );

        businessService.createBusiness(requestDTO);

        ArgumentCaptor<Address> addressArgumentCaptor = ArgumentCaptor.forClass(Address.class);
        //Sometimes 2 addresses saved in this test: one for DGAA (as part of DGAA check on startup), one for this business
        verify(addressRepository, atLeast(1)).save(addressArgumentCaptor.capture());

        ArgumentCaptor<Business> businessArgumentCaptor = ArgumentCaptor.forClass(Business.class);
        verify(businessRepository).save(businessArgumentCaptor.capture());

        Business createdBusiness = businessArgumentCaptor.getValue();

        Assertions.assertEquals(testPrimaryAdmin.getId(), createdBusiness.getPrimaryAdministratorId());
        Assertions.assertEquals(testPrimaryAdmin.getId(), createdBusiness.getAdministrators().get(0).getId());
        Assertions.assertEquals("Lumbridge General Store", createdBusiness.getName());

        Assertions.assertEquals("A one-stop shop for all your adventuring needs", createdBusiness.getDescription());
        Assertions.assertEquals("New Zealand", createdBusiness.getAddress().getCountry());
        Assertions.assertEquals("Accommodation and Food Services", createdBusiness.getBusinessType());
        Assertions.assertEquals("jimsmith@gmail.com", createdBusiness.getAdministrators().get(0).getEmail());
    }

    /**
     * Tries creating a business as a user that doesn't exist
     * Expects a NoUserExistsException
     */
    @Test
    void createBusiness_nonExistentCreator() {
        given(userRepository.findById(600)).willReturn(Optional.empty());

        AddBusinessDTO requestDTO = new AddBusinessDTO(
                "Lumbridge General Store",
                "A one-stop shop for all your adventuring needs",
                businessAddress,
                "Accommodation and Food Services",
                600
        );

        Assertions.assertThrows(NoUserExistsException.class,
                () -> businessService.createBusiness(requestDTO));
    }


    /**
     * Tries creating a business as a user under 16
     * Expects a UserUnderageException
     */
    @Test
    void createBusiness_underageUser() {
        testPrimaryAdmin.setDateOfBirth("2015-04-28");

        AddBusinessDTO requestDTO = new AddBusinessDTO(
                "Lumbridge General Store",
                "A one-stop shop for all your adventuring needs",
                businessAddress,
                "Accommodation and Food Services",
                testPrimaryAdmin.getId()
        );

        Assertions.assertThrows(UserUnderageException.class,
                () -> businessService.createBusiness(requestDTO));
    }


    /**
     * Tries to get a business by calling the service method
     * Checks that we retrieve the correct business
     */
    @Test
    void getBusiness() {

        Business returnedBusiness = businessService.getBusiness(1);

        Assertions.assertEquals(1, returnedBusiness.getId());
        Assertions.assertEquals("Lumbridge General Store", returnedBusiness.getName());
        Assertions.assertEquals(testPrimaryAdmin.getId(), returnedBusiness.getPrimaryAdministratorId());
        Assertions.assertEquals("A one-stop shop for all your adventuring needs", returnedBusiness.getDescription());
        Assertions.assertEquals("Accommodation and Food Services", returnedBusiness.getBusinessType());
    }

    /**
     * Tries to get a business that doesn't exist
     * checks correct exception thrown
     */
    @Test
    void getNonexistentBusiness() {
        given(businessRepository.findById(200)).willReturn(Optional.empty());

        Assertions.assertThrows(BusinessNotFoundException.class,
                () -> businessService.getBusiness(200));
    }


    /**
     * Tries to add an admin
     * Checks that the user is added as an administrator
     */
    @Test
    void addAdministrator() {

        AddOrRemoveBusinessAdminDTO requestDTO = new AddOrRemoveBusinessAdminDTO(
                2
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testPrimaryAdmin));
        businessService.addAdministrator(requestDTO);

        //Check that add administrator method called with user 2
        ArgumentCaptor<User> addedAdministratorCaptor = ArgumentCaptor.forClass(User.class);
        verify(testBusiness).addAdministrator(addedAdministratorCaptor.capture());

        User addedAdmin = addedAdministratorCaptor.getValue();

        Assertions.assertEquals(testUser.getEmail(), addedAdmin.getEmail());
    }


    /**
     * Tries to add an admin that is already an admin
     * Expects a AdministratorAlreadyExistsException
     */
    @Test
    void addExistingAdmin() {
        testBusiness.addAdministrator(testUser);
        given(userRepository.findById(2)).willReturn(Optional.of(testUser));

        AddOrRemoveBusinessAdminDTO requestDTO = new AddOrRemoveBusinessAdminDTO(
                2
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testPrimaryAdmin));

        Assertions.assertThrows(AdministratorAlreadyExistsException.class,
                () -> businessService.addAdministrator(requestDTO));

    }


    /**
     * Random user tries to add an admin
     * Expects a ForbiddenPrimaryAdministratorActionException
     */
    @Test
    void addAdministratorWhenNotPrimaryAdmin() {

        given(userRepository.findById(2)).willReturn(Optional.of(testUser));

        AddOrRemoveBusinessAdminDTO requestDTO = new AddOrRemoveBusinessAdminDTO(
                2
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testUser));

        Assertions.assertThrows(ForbiddenPrimaryAdministratorActionException.class,
                () -> businessService.addAdministrator(requestDTO));

    }


    /**
     * Tries to remove an admin
     * Checks that the administrator is removed
     */
    @Test
    void removeAdministrator() {

        testBusiness.addAdministrator(testUser);
        given(userRepository.findById(2)).willReturn(Optional.of(testUser));

        AddOrRemoveBusinessAdminDTO requestDTO = new AddOrRemoveBusinessAdminDTO(
                2
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testPrimaryAdmin));
        businessService.removeAdministrator(requestDTO);

        //Check that remove administrator method called with user 2
        ArgumentCaptor<User> removedAdministratorCaptor = ArgumentCaptor.forClass(User.class);
        verify(testBusiness).removeAdministrator(removedAdministratorCaptor.capture());

        User removedAdmin = removedAdministratorCaptor.getValue();

        Assertions.assertEquals(testUser.getEmail(), removedAdmin.getEmail());
    }

    /**
     * Tries to remove the primary admin
     * from administrating the business
     * Expect a CantRemoveAdministratorException
     */
    @Test
    void removePrimaryAdministrator() {
        AddOrRemoveBusinessAdminDTO requestDTO = new AddOrRemoveBusinessAdminDTO(
                1
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testPrimaryAdmin));

        Assertions.assertThrows(CantRemoveAdministratorException.class,
                () -> businessService.removeAdministrator(requestDTO));

    }

    /**
     * Tries to remove someone who is not an admin
     * from administrating the business
     * Expect a UserNotAdministratorException
     */
    @Test
    void removeNonExistentAdministrator() {
        AddOrRemoveBusinessAdminDTO requestDTO = new AddOrRemoveBusinessAdminDTO(
                2
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testPrimaryAdmin));

        Assertions.assertThrows(UserNotAdministratorException.class,
                () -> businessService.removeAdministrator(requestDTO));
    }

    /**
     * Random user tries to remove
     * from administrating the business
     *
     * Expect a ForbiddenPrimaryAdministratorActionException
     */
    @Test
    void removeAdministratorWhenNotPrimaryAdmin() {

        testBusiness.addAdministrator(testUser);
        given(userRepository.findById(2)).willReturn(Optional.of(testUser));

        AddOrRemoveBusinessAdminDTO requestDTO = new AddOrRemoveBusinessAdminDTO(
                2
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testUser));

        Assertions.assertThrows(ForbiddenPrimaryAdministratorActionException.class,
                () -> businessService.removeAdministrator(requestDTO));

    }

    /**
     * Tests that searching a business by exact name succeeds
     */
    @Test
    void searchBusiness_exactNameMatch() {
        given(businessRepository.findAll(any(Specification.class)))
                .willReturn(List.of(testBusiness));

        SearchBusinessDTO requestDTO = new SearchBusinessDTO(testBusiness.getName(), null);

        List<Business> retrievedBusinesses = businessService.searchBusiness(requestDTO);

        Assertions.assertEquals(1, retrievedBusinesses.size());
        Assertions.assertEquals(testBusiness.getName(), retrievedBusinesses.get(0).getName());
    }

    /**
     * Tests that when there are multiple matches by name,
     * filtering them by business type works
     */
    @Test
    void searchBusiness_filterByType() {
        Business otherBusiness = new Business(testBusiness.getName(), "A one-stop shop for all your adventuring needs",
                null, "Retail Trade", 1);

        given(businessRepository.findAll(any(Specification.class)))
                .willReturn(List.of(testBusiness, otherBusiness));

        SearchBusinessDTO requestDTO = new SearchBusinessDTO(testBusiness.getName(),
                BusinessType.getType(testBusiness.getBusinessType())); //"Accommodation and Food Services"

        List<Business> retrievedBusinesses = businessService.searchBusiness(requestDTO);

        Assertions.assertEquals(1, retrievedBusinesses.size());
        Assertions.assertEquals(testBusiness.getBusinessType(), retrievedBusinesses.get(0).getBusinessType());
    }

}
