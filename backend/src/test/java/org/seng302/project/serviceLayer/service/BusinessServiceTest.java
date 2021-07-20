package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.model.types.BusinessType;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.address.AddressDTO;
import org.seng302.project.serviceLayer.dto.business.GetBusinessDTO;
import org.seng302.project.serviceLayer.dto.business.PostBusinessDTO;
import org.seng302.project.serviceLayer.dto.business.PutBusinessAdminDTO;
import org.seng302.project.serviceLayer.exceptions.ForbiddenException;
import org.seng302.project.serviceLayer.exceptions.NoUserExistsException;
import org.seng302.project.serviceLayer.exceptions.NotAcceptableException;
import org.seng302.project.serviceLayer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.AdministratorAlreadyExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.CantRemoveAdministratorException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.UserNotAdministratorException;
import org.seng302.project.serviceLayer.exceptions.register.UserUnderageException;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

class BusinessServiceTest extends AbstractInitializer {

    private BusinessService businessService;

    private AddressRepository addressRepository;
    private UserRepository userRepository;
    private BusinessRepository businessRepository;

    private User testPrimaryAdmin;
    private User testUser;
    private User testOtherUser;
    private User testSystemAdmin;
    private Business testBusiness;
    private AddressDTO businessAddress;

    @BeforeEach
    public void setup() {
        addressRepository = Mockito.mock(AddressRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        businessRepository = Mockito.mock(BusinessRepository.class);
        businessService = new BusinessService(businessRepository, addressRepository, userRepository);

        //Mock a test user to be used as business primary admin
        testPrimaryAdmin = this.getTestUserBusinessAdmin();
        Mockito.when(userRepository.findById(testPrimaryAdmin.getId())).thenReturn(Optional.of(testPrimaryAdmin));
        Mockito.when(userRepository.findByEmail(testPrimaryAdmin.getEmail())).thenReturn(List.of(testPrimaryAdmin));

        testSystemAdmin = this.getTestSystemAdmin();
        Mockito.when(userRepository.findById(testSystemAdmin.getId())).thenReturn(Optional.of(testSystemAdmin));
        Mockito.when(userRepository.findByEmail(testSystemAdmin.getEmail())).thenReturn(List.of(testSystemAdmin));

        //Mock a different test user
        testUser = this.getTestUser();
        Mockito.when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.findByEmail(testUser.getEmail())).thenReturn(List.of(testUser));

        testOtherUser = this.getTestOtherUser();
        Mockito.when(userRepository.findById(testOtherUser.getId())).thenReturn(Optional.of(testOtherUser));
        Mockito.when(userRepository.findByEmail(testOtherUser.getEmail())).thenReturn(List.of(testOtherUser));

        businessAddress = new AddressDTO("", "", "", "", "New Zealand", "");

        //Mock a test business
        //Spy on this business so we can check when methods on the business object are called
        testBusiness = Mockito.spy(this.getTestBusiness());
        Mockito.when(businessRepository.findById(testBusiness.getId())).thenReturn(Optional.of(testBusiness));
        Mockito.when(businessRepository.findByName(testBusiness.getName())).thenReturn(List.of(testBusiness));

    }

    /**
     * Creates the test business from the API
     * Checks that the new business and its address are saved
     */
    @Test
    void createBusiness_success() {

        PostBusinessDTO requestDTO = new PostBusinessDTO(
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
        Assertions.assertEquals(testPrimaryAdmin.getEmail(), createdBusiness.getAdministrators().get(0).getEmail());
    }

    /**
     * Tries creating a business as a user that doesn't exist
     * Expects a NoUserExistsException
     */
    @Test
    void createBusiness_nonExistentCreator() {
        Mockito.when(userRepository.findById(600)).thenReturn(Optional.empty());

        PostBusinessDTO requestDTO = new PostBusinessDTO(
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

        PostBusinessDTO requestDTO = new PostBusinessDTO(
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

        GetBusinessDTO returnedBusiness = businessService.getBusiness(testBusiness.getId());

        Assertions.assertEquals(testBusiness.getId(), returnedBusiness.getId());
        Assertions.assertEquals(testBusiness.getName(), returnedBusiness.getName());
        Assertions.assertEquals(testPrimaryAdmin.getId(), returnedBusiness.getPrimaryAdministratorId());
        Assertions.assertEquals(testBusiness.getDescription(), returnedBusiness.getDescription());
        Assertions.assertEquals(testBusiness.getBusinessType(), returnedBusiness.getBusinessType());
    }

    /**
     * Tries to get a business that doesn't exist
     * checks correct exception thrown
     */
    @Test
    void getNonExistentBusiness() {
        Mockito.when(businessRepository.findById(200)).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessNotFoundException.class,
                () -> businessService.getBusiness(200));
    }


    /**
     * Tries to add an admin
     * Checks that the user is added as an administrator
     */
    @Test
    void addAdministrator() {

        PutBusinessAdminDTO requestDTO = new PutBusinessAdminDTO(
                testUser.getId()
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

        PutBusinessAdminDTO requestDTO = new PutBusinessAdminDTO(
                testUser.getId()
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testPrimaryAdmin));

        Assertions.assertThrows(AdministratorAlreadyExistsException.class,
                () -> businessService.addAdministrator(requestDTO));

    }


    /**
     * Random user tries to add an admin
     * Expects a ForbiddenException
     */
    @Test
    void addAdministratorWhenNotPrimaryAdmin() {
        PutBusinessAdminDTO requestDTO = new PutBusinessAdminDTO(
                testUser.getId()
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testOtherUser));

        Assertions.assertThrows(ForbiddenException.class,
                () -> businessService.addAdministrator(requestDTO));

    }


    /**
     * Tries to remove an admin
     * Checks that the administrator is removed
     */
    @Test
    void removeAdministrator() {

        testBusiness.addAdministrator(testUser);

        PutBusinessAdminDTO requestDTO = new PutBusinessAdminDTO(
                testUser.getId()
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
        PutBusinessAdminDTO requestDTO = new PutBusinessAdminDTO(
                testPrimaryAdmin.getId()
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
        PutBusinessAdminDTO requestDTO = new PutBusinessAdminDTO(
                testUser.getId()
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testPrimaryAdmin));

        Assertions.assertThrows(UserNotAdministratorException.class,
                () -> businessService.removeAdministrator(requestDTO));
    }

    /**
     * Random user tries to remove
     * from administrating the business
     * <p>
     * Expect a ForbiddenException
     */
    @Test
    void removeAdministratorWhenNotPrimaryAdmin() {

        testBusiness.addAdministrator(testUser);

        PutBusinessAdminDTO requestDTO = new PutBusinessAdminDTO(
                testUser.getId()
        );
        requestDTO.setBusinessId(testBusiness.getId());
        requestDTO.setAppUser(new AppUserDetails(testOtherUser));

        Assertions.assertThrows(ForbiddenException.class,
                () -> businessService.removeAdministrator(requestDTO));

    }

    /**
     * Tests that searching a business by exact name succeeds
     */
    @Test
    void searchBusiness_exactNameMatch() {
        Mockito.when(businessRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(testBusiness));

        List<Business> retrievedBusinesses = businessService.searchBusiness(testBusiness.getName(), null);

        Assertions.assertEquals(1, retrievedBusinesses.size());
        Assertions.assertEquals(testBusiness.getName(), retrievedBusinesses.get(0).getName());
    }

    /**
     * Tests that when there are multiple matches by name,
     * filtering them by business type works
     */
    @Test
    void searchBusiness_filterByType() {
        Business otherBusiness = new Business("Another Business", "Some Description",
                null, "Accommodation and Food Services", testPrimaryAdmin.getId());

        Mockito.when(businessRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(testBusiness, otherBusiness));

        BusinessType type = BusinessType.getType(testBusiness.getBusinessType());
        List<Business> retrievedBusinesses = businessService.searchBusiness(testBusiness.getName(), type);

        Assertions.assertEquals(1, retrievedBusinesses.size());
        Assertions.assertEquals(testBusiness.getBusinessType(), retrievedBusinesses.get(0).getBusinessType());
    }

    /**
     * Tests that modifying a business with valid new details results in success
     */
    @Test
    void editBusiness_validRequest_success() {
        Mockito.when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(businessRepository.save(any(Business.class)))
                .thenAnswer(invocation -> {
                    testBusiness = invocation.getArgument(0);
                    return testBusiness;
                });

        String newName = "New Business Name";
        String newDescription = "A new description for this business";
        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);

        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );
        requestDTO.setName(newName);
        requestDTO.setDescription(newDescription);

        businessService.editBusiness(requestDTO, testBusiness.getId(), appUser);

        Assertions.assertEquals(newName, testBusiness.getName());
        Assertions.assertEquals(newDescription, testBusiness.getDescription());
    }

    /**
     * Tests that modifying a business with valid new details as GAA results in success
     */
    @Test
    void editBusiness_validRequestAsGAA_success() {
        Mockito.when(addressRepository.save(any(Address.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(businessRepository.save(any(Business.class)))
                .thenAnswer(invocation -> {
                    testBusiness = invocation.getArgument(0);
                    return testBusiness;
                });

        String newName = "New Business Name";
        String newDescription = "A new description for this business";
        AppUserDetails appUser = new AppUserDetails(testSystemAdmin);

        PostBusinessDTO requestDTO = new PostBusinessDTO(
                testBusiness.getName(),
                testBusiness.getDescription(),
                new AddressDTO(testBusiness.getAddress()),
                testBusiness.getBusinessType(),
                testBusiness.getPrimaryAdministratorId()
        );
        requestDTO.setName(newName);
        requestDTO.setDescription(newDescription);

        businessService.editBusiness(requestDTO, testBusiness.getId(), appUser);

        Assertions.assertEquals(newName, testBusiness.getName());
        Assertions.assertEquals(newDescription, testBusiness.getDescription());
    }

    /**
     * Tests that making an edit business request to a non-existent business results in an error
     */
    @Test
    void editBusiness_nonExistentBusiness_throwsException() {
        PostBusinessDTO requestDTO = new PostBusinessDTO();
        AppUserDetails appUser = new AppUserDetails(testPrimaryAdmin);

        Assertions.assertThrows(NotAcceptableException.class,
                () -> businessService.editBusiness(requestDTO, 100, appUser));
    }

    /**
     * Tests that trying to edit a business as a non-business admin results in error
     */
    @Test
    void editBusiness_notAdmin_throwsException() {
        PostBusinessDTO requestDTO = new PostBusinessDTO();
        AppUserDetails appUser = new AppUserDetails(testUser);

        Integer businessId = testBusiness.getId();
        Assertions.assertThrows(ForbiddenException.class,
                () -> businessService.editBusiness(requestDTO, businessId, appUser));
    }

}
