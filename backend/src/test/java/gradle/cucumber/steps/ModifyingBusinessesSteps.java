package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.address.AddressDTO;
import org.seng302.project.serviceLayer.dto.business.PostBusinessDTO;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ModifyingBusinessesSteps extends AbstractInitializer {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private User testBusinessAdmin;
    private Business testBusiness;
    private PostBusinessDTO requestDTO;
    private RequestBuilder request;

    @Autowired
    public ModifyingBusinessesSteps(UserRepository userRepository,
                                    AddressRepository addressRepository,
                                    BusinessRepository businessRepository,
                                    ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.businessRepository = businessRepository;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context, CardRepository cardRepository) {
        this.initialise();
        cardRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testBusinessAdmin = this.getTestUserBusinessAdmin();
        testBusinessAdmin.setId(null);
        testBusinessAdmin.getHomeAddress().setId(null);
        addressRepository.save(testBusinessAdmin.getHomeAddress());
        userRepository.save(testBusinessAdmin);

        testBusiness = this.getTestBusiness();
        testBusiness.setId(null);
        testBusiness.getAddress().setId(null);
        testBusiness.setAdministrators(new ArrayList<>());
        addressRepository.save(testBusiness.getAddress());
        businessRepository.save(testBusiness);
    }

    @Given("I am the administrator of a business account")
    public void i_am_the_administrator_of_a_business_account() {
        testBusiness.addAdministrator(testBusinessAdmin);
        testBusiness = businessRepository.save(testBusiness);
        Assertions.assertTrue(testBusiness.userIsAdmin(testBusinessAdmin.getId()));
    }

    @When("I try to edit the business details to:")
    public void i_try_to_edit_the_business_details_to(DataTable dataTable) throws Exception {
        List<Map<String, String>> mapList = dataTable.asMaps();
        Address newAddress = testBusiness.getAddress();
        newAddress.setCountry(mapList.get(0).get("country"));
        requestDTO = new PostBusinessDTO(
                mapList.get(0).get("name"),
                mapList.get(0).get("description"),
                new AddressDTO(newAddress),
                mapList.get(0).get("type"),
                testBusiness.getPrimaryAdministratorId()
        );

        request = MockMvcRequestBuilders
                .put("/businesses/{businessId}", testBusiness.getId())
                .content(objectMapper.writeValueAsString(requestDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testBusinessAdmin)));
    }

    @Then("The business details are updated")
    public void the_business_details_are_updated() throws Exception {
        mockMvc.perform(request).andExpect(status().isOk());
        testBusiness = businessRepository.findById(testBusiness.getId()).orElseThrow();
        Assertions.assertEquals(requestDTO.getName(), testBusiness.getName());
        Assertions.assertEquals(requestDTO.getDescription(), testBusiness.getDescription());
        Assertions.assertEquals(requestDTO.getAddress().getCountry(), testBusiness.getAddress().getCountry());
        Assertions.assertEquals(requestDTO.getBusinessType(), testBusiness.getBusinessType());
    }

    @Then("A Bad Request status is returned to the user")
    public void a_bad_request_status_is_returned_to_the_user() throws Exception {
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    @When("I upload a new image for the business")
    public void i_upload_a_new_image_for_the_business() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The image is saved to the repository for the business")
    public void the_image_is_saved_to_the_repository_for_the_business() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("The business has at least {int} images, the first being the primary image")
    public void the_business_has_at_least_images_the_first_being_the_primary_image(Integer numImages) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("I change the primary image to be the second image")
    public void i_change_the_primary_image_to_be_the_second_image() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The business' primary image is the second image")
    public void the_business_primary_image_is_the_second_image() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("A thumbnail of the new primary image is created")
    public void a_thumbnail_of_the_new_primary_image_is_created() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}