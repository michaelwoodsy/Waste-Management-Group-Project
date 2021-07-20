package gradle.cucumber.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.serviceLayer.dto.product.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.product.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.repositoryLayer.repository.AddressRepository;
import org.seng302.project.repositoryLayer.repository.CardRepository;
import org.seng302.project.repositoryLayer.repository.KeywordRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.product.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.product.DeleteProductImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.DeleteUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.PutUserDTO;
import org.seng302.project.serviceLayer.service.UserImageService;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.sql.Array;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

public class ModifyingIndividualsSteps extends AbstractInitializer {

    private User testUser;
    private PutUserDTO putUserDTO;
    private MockMultipartFile testImageFile;

    private RequestBuilder editUserRequest;

    private MockMvc mockMvc;

    private ResultActions reqResult;

    private Image deletedImage;

    private final UserImageService userImageService;

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final CardRepository cardRepository;
    private final KeywordRepository keywordRepository;
    private final ObjectMapper objectMapper;

    private final SpringEnvironment springEnvironment;



    @Autowired
    public ModifyingIndividualsSteps(UserRepository userRepository,
                                     BCryptPasswordEncoder passwordEncoder,
                                     AddressRepository addressRepository,
                                     CardRepository cardRepository,
                                     KeywordRepository keywordRepository,
                                     ObjectMapper objectMapper,
                                     UserImageService userImageService,
                                     ImageRepository imageRepository,
                                     SpringEnvironment springEnvironment) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
        this.cardRepository = cardRepository;
        this.keywordRepository = keywordRepository;
        this.objectMapper = objectMapper;
        this.userImageService = userImageService;
        this.springEnvironment = springEnvironment;
        this.imageRepository = imageRepository;

    }

    @BeforeEach
    @Autowired
    public void setUp(WebApplicationContext context) {
        initialise();
        cardRepository.deleteAll();
        keywordRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();

        testImageFile = this.getTestImageFile();

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        this.initialiseTestFiles();
        testImageFile = this.getTestImageFile();

        testUser = this.getTestUser();
        addressRepository.save(testUser.getHomeAddress());
        testUser.setRole("user");
        testUser.setId(null);//Set id to null so userRepository can set it
        userRepository.save(testUser);
    }


    //AC1: As a registered individual, I can update any of my attributes.

    @Given("I am logged in as a user")
    public void iAmLoggedInAsAUser() {
        Assertions.assertEquals(1, userRepository.findByEmail(testUser.getEmail()).size());
    }


    @When("I try to edit my account to the details:")
    public void iTryToEditMyAccountToTheDetails(io.cucumber.datatable.DataTable dataTable) throws Exception {
        List<Map<String, String>> userMap = dataTable.asMaps(String.class, String.class);
        putUserDTO = new PutUserDTO(
                testUser.getId(),
                userMap.get(0).get("firstName"),
                userMap.get(0).get("lastName"),
                userMap.get(0).get("middleName"),
                userMap.get(0).get("nickname"),
                userMap.get(0).get("bio"),
                userMap.get(0).get("email"),
                userMap.get(0).get("dateOfBirth"),
                userMap.get(0).get("phoneNumber"),
                testUser.getHomeAddress(),
                userMap.get(0).get("password"),
                "password");

        this.mockMvc.perform(MockMvcRequestBuilders
                .put("/users/{id}", putUserDTO.getId())
                .content(objectMapper.writeValueAsString(putUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser))))
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();
    }

    @Then("My details are updated.")
    public void myDetailsAreUpdated() {
        Optional<User> editedUserOptions = userRepository.findById(putUserDTO.getId());

        Assertions.assertTrue(editedUserOptions.isPresent());
        User editedUser = editedUserOptions.get();

        Assertions.assertEquals(putUserDTO.getFirstName(), editedUser.getFirstName());
        Assertions.assertEquals(putUserDTO.getLastName(), editedUser.getLastName());
        Assertions.assertEquals(putUserDTO.getMiddleName(), editedUser.getMiddleName());
        Assertions.assertEquals(putUserDTO.getNickname(), editedUser.getNickname());
        Assertions.assertEquals(putUserDTO.getBio(), editedUser.getBio());
        Assertions.assertEquals(putUserDTO.getEmail(), editedUser.getEmail());
        Assertions.assertEquals(putUserDTO.getDateOfBirth(), editedUser.getDateOfBirth());
        Assertions.assertEquals(putUserDTO.getPhoneNumber(), editedUser.getPhoneNumber());
        Assertions.assertEquals(putUserDTO.getHomeAddress().getCountry(), editedUser.getHomeAddress().getCountry());

        passwordEncoder.matches(putUserDTO.getNewPassword(), editedUser.getPassword());
    }

    
    //AC2: All validation rules still apply. For example, I can only modify my date of birth if I still remain over the required age.

    @When("I try to edit my date of birth to {int} years ago")
    public void iTryToEditMyDateOfBirthToYearsAgo(int years) throws Exception {
        putUserDTO = new PutUserDTO(
            testUser.getId(),
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                testUser.getEmail(),
                LocalDateTime.now().minusYears(years).toLocalDate().toString(),
                testUser.getPhoneNumber(),
                testUser.getHomeAddress(),
                "Password123",
                "password"
        );

        editUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", putUserDTO.getId())
                .content(objectMapper.writeValueAsString(putUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }

    @Then("An error message is returned to say the date of birth is too young.")
    public void anErrorMessageIsReturnedToSayTheDateOfBirthIsTooYoung() throws Exception {
        MvcResult editUserResponse = this.mockMvc.perform(editUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = editUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("DateOfBirthInvalid: This Date of Birth is not valid.", returnedExceptionString);
    }


    //AC3: Mandatory attributes still remain mandatory.

    @When("I try to edit my account and dont enter in a first name")
    public void iTryToEditMyAccountAndDontEnterInAFirstName() throws Exception {
        putUserDTO = new PutUserDTO(
                testUser.getId(),
                null,
                testUser.getLastName(),
                testUser.getMiddleName(),
                testUser.getNickname(),
                testUser.getBio(),
                testUser.getEmail(),
                testUser.getDateOfBirth(),
                testUser.getPhoneNumber(),
                testUser.getHomeAddress(),
                "Password123",
                "password"
        );

        editUserRequest = MockMvcRequestBuilders
                .put("/users/{id}", putUserDTO.getId())
                .content(objectMapper.writeValueAsString(putUserDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(testUser)));
    }

    @Then("An error message is shown saying the first name is required.")
    public void anErrorMessageIsShownSayingTheFirstNameIsRequired() throws Exception {
        MvcResult editUserResponse = this.mockMvc.perform(editUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = editUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals("MissingData: First Name is a mandatory field", returnedExceptionString);
    }

    @Given("A user has no images")
    public void aUserHasNoImages() {
        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(0, retrievedUser.getImages().size());
    }

    @When("I upload an image for a user")
    public void iUploadAnImageForAUser() {

        AddUserImageDTO dto = new AddUserImageDTO(
                testUser.getId(),
                new AppUserDetails(testUser),
                testImageFile
        );
        userImageService.addUserImage(dto);
    }

    @Then("The image is saved in the repository on the user")
    public void theImageIsSavedInTheRepositoryOnTheUser() {
        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(1, retrievedUser.getImages().size());
        Image image = retrievedUser.getImages().iterator().next();

        File imageFile = new File(springEnvironment.getMediaFolderPath() + image.getFilename());
        File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image.getThumbnailFilename());
        Assertions.assertTrue(imageFile.delete());
        Assertions.assertTrue(imageThumbnailFile.delete());
    }

    @Given("A user has an image")
    public void aUserHasAnImage() {
        AddUserImageDTO dto = new AddUserImageDTO (
                testUser.getId(),
                new AppUserDetails(testUser),
                testImageFile
        );
        userImageService.addUserImage(dto);

        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(1, retrievedUser.getImages().size());
        testUser = retrievedUser;
    }

    @When("I delete an image for a user")
    public void iDeleteAnImageForAUser() {
        Image image = testUser.getImages().iterator().next();
        deletedImage = image;
        DeleteUserImageDTO dto = new DeleteUserImageDTO(
                testUser.getId(),
                image.getId(),
                new AppUserDetails(testUser));

        userImageService.deleteImage(dto);
    }

    @Then("The user no longer has that image as one of it's images")
    public void theUserNoLongerHasThatImageAsOneOfItSImages() {
        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(0, retrievedUser.getImages().size());
    }

    @Then("The user's image is no longer saved")
    public void theUsersImageIsNoLongerSaved() {
        File imageFile = new File(springEnvironment.getMediaFolderPath() + deletedImage.getFilename());
        File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + deletedImage.getThumbnailFilename());
        Assertions.assertFalse(imageFile.exists());
        Assertions.assertFalse(imageThumbnailFile.exists());
    }

    //AC6 - Changing primary image
    @Given("A user has at least {int} images the first is the primary image")
    public void aUserHasAtLeastImagesTheFirstIsThePrimaryImage(int numberOfImages) {
        // numberOfImages = 2 so we add 2 images
        AddUserImageDTO dto = new AddUserImageDTO(
                testUser.getId(),
                new AppUserDetails(testUser),
                testImageFile);
        //Image 1
        userImageService.addUserImage(dto);
        //Image 2
        userImageService.addUserImage(dto);

        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(numberOfImages, retrievedUser.getImages().size());
        testUser = retrievedUser;
    }

    @When("The user changes the primary image to be the second image")
    public void theUserChangesThePrimaryImageToBeTheSecondImage() {
        //Add all image from set to arraylist for the testUser
        ArrayList<Image> imageList = new ArrayList<>(testUser.getImages());

        userImageService.setPrimaryImage(testUser.getId(), imageList.get(1).getId(), new AppUserDetails(testUser));
    }

    @Then("The primary image for the user is the second image")
    public void thePrimaryImageForTheUserIsTheSecondImage() {
        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();

        //Add all images from set to arraylist for the testUser
        ArrayList<Image> imageListTestUser = new ArrayList<>(testUser.getImages());

        Assertions.assertEquals(imageListTestUser.get(1).getId(), retrievedUser.getPrimaryImageId());

        //Remove images from system to clean up
        Image image1 = imageListTestUser.get(0);
        File image1File = new File(springEnvironment.getMediaFolderPath() + image1.getFilename());
        File image1ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image1.getThumbnailFilename());
        Assertions.assertTrue(image1File.delete());
        Assertions.assertTrue(image1ThumbnailFile.delete());
        Image image2 = imageListTestUser.get(1);
        File image2File = new File(springEnvironment.getMediaFolderPath() + image2.getFilename());
        File image2ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image2.getThumbnailFilename());
        Assertions.assertTrue(image2File.delete());
        Assertions.assertTrue(image2ThumbnailFile.delete());
    }

    @Then("The uploaded image is the primary image for the user")
    public void theUploadedImageIsThePrimaryImageForTheUser() {
        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(1, retrievedUser.getImages().size());

        //Add all images from set to arraylist for the retrievedUser
        ArrayList<Image> imageList = new ArrayList<>(retrievedUser.getImages());

        //Check that the first image in the list of images for the user has the same Id as the primary image
        Assertions.assertEquals(imageList.get(0).getId(), retrievedUser.getPrimaryImageId());

        Image image = imageList.get(0);
        //Remove images from system to clean up
        File imageFile = new File(springEnvironment.getMediaFolderPath() + image.getFilename());
        File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image.getThumbnailFilename());
        Assertions.assertTrue(imageFile.delete());
        Assertions.assertTrue(imageThumbnailFile.delete());
    }

    //AC7 : A thumbnail of the primary image is automatically created

    @Given("A user has {int} images")
    public void aUserHasImages(int numberOfImages) {
        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(numberOfImages, retrievedUser.getImages().size());
        testUser = retrievedUser;
    }

    @When("An image is uploaded")
    public void anImageIsUploaded() {
        AddUserImageDTO dto = new AddUserImageDTO(
                testUser.getId(),
                new AppUserDetails(testUser),
                testImageFile);
        userImageService.addUserImage(dto);
    }

    @Then("The uploaded image has a thumbnail created")
    public void theUploadedImageHasAThumbnailCreated() {
        Optional<User> optionalUser = userRepository.findById(testUser.getId());
        Assertions.assertTrue(optionalUser.isPresent());
        User retrievedUser = optionalUser.get();
        Assertions.assertEquals(1, retrievedUser.getImages().size());

        //Add all images from set to arraylist for the retrievedUser
        ArrayList<Image> imageList = new ArrayList<>(retrievedUser.getImages());

        //Check that the image has a thumbnail
        Image image = imageList.get(0);
        Assertions.assertNotNull(image.getThumbnailFilename());

        //Remove images from system to clean up
        File imageFile = new File(springEnvironment.getMediaFolderPath() + image.getFilename());
        File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image.getThumbnailFilename());
        Assertions.assertTrue(imageFile.delete());
        Assertions.assertTrue(imageThumbnailFile.delete());
    }


}
