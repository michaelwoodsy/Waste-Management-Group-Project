package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Address;
import org.seng302.project.repository_layer.model.Business;
import org.seng302.project.repository_layer.model.Image;
import org.seng302.project.repository_layer.model.User;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.address.AddressDTO;
import org.seng302.project.service_layer.dto.business.AddBusinessImageDTO;
import org.seng302.project.service_layer.dto.business.PostBusinessDTO;
import org.seng302.project.service_layer.service.BusinessImageService;
import org.seng302.project.service_layer.util.SpringEnvironment;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
public class ModifyingBusinessesSteps extends AbstractInitializer {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ImageRepository imageRepository;
    private final ObjectMapper objectMapper;

    private final SpringEnvironment springEnvironment;

    private final BusinessImageService businessImageService;

    private MockMvc mockMvc;
    private User testBusinessAdmin;
    private Business testBusiness;
    private PostBusinessDTO requestDTO;
    private RequestBuilder request;

    private MockMultipartFile testImageFile;

    @Autowired
    public ModifyingBusinessesSteps(UserRepository userRepository,
                                    AddressRepository addressRepository,
                                    BusinessRepository businessRepository,
                                    ImageRepository imageRepository,
                                    ObjectMapper objectMapper,
                                    SpringEnvironment springEnvironment,
                                    BusinessImageService businessImageService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.businessRepository = businessRepository;
        this.imageRepository = imageRepository;
        this.objectMapper = objectMapper;
        this.springEnvironment = springEnvironment;
        this.businessImageService = businessImageService;
    }

    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context,
                      CardRepository cardRepository,
                      SaleListingRepository saleListingRepository) {
        this.initialise();
        cardRepository.deleteAll();
        saleListingRepository.deleteAll();
        businessRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();

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

        testImageFile = this.getTestImageFile();
    }

    //AC1

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

    //AC2

    @Then("A Bad Request status is returned to the user")
    public void a_bad_request_status_is_returned_to_the_user() throws Exception {
        mockMvc.perform(request).andExpect(status().isBadRequest());
    }

    //AC4

    @When("I upload a new image for the business")
    public void i_upload_a_new_image_for_the_business() {
        AddBusinessImageDTO dto = new AddBusinessImageDTO(
                testBusiness.getId(),
                new AppUserDetails(testBusinessAdmin),
                testImageFile
        );
        businessImageService.addBusinessImage(dto);
    }

    @Then("The image is saved to the repository for the business")
    public void the_image_is_saved_to_the_repository_for_the_business() {
        Optional<Business> optionalBusiness = businessRepository.findById(testBusiness.getId());
        Assertions.assertTrue(optionalBusiness.isPresent());
        Business retrievedBusiness = optionalBusiness.get();

        Assertions.assertEquals(1, retrievedBusiness.getImages().size());
        Image image = retrievedBusiness.getImages().get(0);

        File imageFile = new File(springEnvironment.getMediaFolderPath() + image.getFilename());
        File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image.getThumbnailFilename());
        Assertions.assertTrue(imageFile.delete());
        Assertions.assertTrue(imageThumbnailFile.delete());
    }

    //AC5

    @Given("The business has at least {int} images, the first being the primary image")
    public void the_business_has_at_least_images_the_first_being_the_primary_image(Integer numberOfImages) {
        // numberOfImages = 2 so we add 2 images
        AddBusinessImageDTO dto = new AddBusinessImageDTO(
                testBusiness.getId(),
                new AppUserDetails(testBusinessAdmin),
                testImageFile
        );
        //Image 1
        businessImageService.addBusinessImage(dto);
        //Image 2
        businessImageService.addBusinessImage(dto);

        Optional<Business> optionalBusiness = businessRepository.findById(testBusiness.getId());
        Assertions.assertTrue(optionalBusiness.isPresent());
        Business retrivedBusiness = optionalBusiness.get();
        Assertions.assertEquals(numberOfImages, retrivedBusiness.getImages().size());
        testBusiness = retrivedBusiness;
    }

    @When("I change the primary image to be the second image")
    public void i_change_the_primary_image_to_be_the_second_image() {
        businessImageService.setPrimaryImage(
                testBusiness.getId(),
                testBusiness.getImages().get(1).getId(),
                new AppUserDetails(testBusinessAdmin));
    }

    @Then("The business' primary image is the second image")
    public void the_business_primary_image_is_the_second_image() {
        Optional<Business> optionalBusiness = businessRepository.findById(testBusiness.getId());
        Assertions.assertTrue(optionalBusiness.isPresent());
        Business retrievedBusiness = optionalBusiness.get();

        Assertions.assertEquals(testBusiness.getImages().get(1), retrievedBusiness.getImages().get(1));

        //Remove images and clean up
        Image image1 = testBusiness.getImages().get(0);
        File image1File = new File(springEnvironment.getMediaFolderPath() + image1.getFilename());
        File image1ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image1.getThumbnailFilename());
        Assertions.assertTrue(image1File.delete());
        Assertions.assertTrue(image1ThumbnailFile.delete());

        Image image2 = testBusiness.getImages().get(1);
        File image2File = new File(springEnvironment.getMediaFolderPath() + image2.getFilename());
        File image2ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image2.getThumbnailFilename());
        Assertions.assertTrue(image2File.delete());
        Assertions.assertTrue(image2ThumbnailFile.delete());
    }

    //AC6

    @Then("A thumbnail of the new primary image is created")
    public void a_thumbnail_of_the_new_primary_image_is_created() {
        Optional<Business> optionalBusiness = businessRepository.findById(testBusiness.getId());
        Assertions.assertTrue(optionalBusiness.isPresent());
        Business retrievedBusiness = optionalBusiness.get();

        Assertions.assertNotNull(retrievedBusiness.getImages().get(1).getThumbnailFilename());

        //Remove images and clean up
        Image image1 = testBusiness.getImages().get(0);
        File image1File = new File(springEnvironment.getMediaFolderPath() + image1.getFilename());
        File image1ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image1.getThumbnailFilename());
        Assertions.assertTrue(image1File.delete());
        Assertions.assertTrue(image1ThumbnailFile.delete());

        Image image2 = testBusiness.getImages().get(1);
        File image2File = new File(springEnvironment.getMediaFolderPath() + image2.getFilename());
        File image2ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image2.getThumbnailFilename());
        Assertions.assertTrue(image2File.delete());
        Assertions.assertTrue(image2ThumbnailFile.delete());
    }

}