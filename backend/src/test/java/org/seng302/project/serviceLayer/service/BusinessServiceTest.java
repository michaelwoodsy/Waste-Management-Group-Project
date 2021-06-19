package org.seng302.project.serviceLayer.service;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.AddBusinessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BusinessServiceTest {

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

        AddBusinessDTO requestDTO = new AddBusinessDTO (
                "Lumbridge General Store",
                "A one-stop shop for all your adventuring needs",
                businessAddress,
                "Accommodation and Food Services",
                testPrimaryAdmin.getId()
        );

        businessService.createBusiness(requestDTO);

        ArgumentCaptor<Address> addressArgumentCaptor = ArgumentCaptor.forClass(Address.class);
        //2 addresses saved in this test: one for DGAA (as part of DGAA check on startup), one for this business
        verify(addressRepository, times(2)).save(addressArgumentCaptor.capture());

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

    //Create business with non-existent user id

    //Create business with underage user

    //

}
