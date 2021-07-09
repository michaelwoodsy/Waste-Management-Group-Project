package gradle.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.*;
import org.seng302.project.repositoryLayer.repository.*;
import org.seng302.project.serviceLayer.dto.product.AddProductImageDTO;
import org.seng302.project.serviceLayer.dto.product.DeleteProductImageDTO;
import org.seng302.project.serviceLayer.dto.product.SetPrimaryProductImageDTO;
import org.seng302.project.serviceLayer.service.ProductImageService;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Cucumber steps for UC16 - Product Images
 */
public class ProductImagesSteps extends AbstractInitializer {

    private User businessOwner;
    private String businessOwnerEmail;
    private String businessOwnerPassword;

    private Business business;

    private Address address;

    private MockMultipartFile testImageFile;

    private Product product;

    private Image deletedImage;


    private final ProductImageService productImageService;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final BusinessRepository businessRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final CardRepository cardRepository;

    private final SpringEnvironment springEnvironment;

    @Autowired
    ProductImagesSteps(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                       BusinessRepository businessRepository, AddressRepository addressRepository,
                       ProductRepository productRepository, ImageRepository imageRepository, 
                       ProductImageService productImageService, CardRepository cardRepository,
                       SpringEnvironment springEnvironment) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.businessRepository = businessRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.productImageService = productImageService;
        this.cardRepository = cardRepository;
        this.springEnvironment = springEnvironment;
    }

    /**
     * Initialise MockMvc and some test users
     */
    @BeforeEach
    @Autowired
    public void setup(WebApplicationContext context) {

        productRepository.deleteAll();
        businessRepository.deleteAll();
        cardRepository.deleteAll();
        userRepository.deleteAll();
        imageRepository.deleteAll();
        addressRepository.deleteAll();

        this.initialiseTestFiles();
        testImageFile = this.getTestImageFile();

        address = new Address("", "", "", "", "New Zealand", "");
        businessOwnerEmail = "b.beetle@gmail.com";
        businessOwnerPassword = "B0bLovesB33tles";
        businessOwner = new User("Bob", "Beetle", "", "", "",
                businessOwnerEmail, "2000-05-21", "+64 3 555 0129",
                address, businessOwnerPassword);
        addressRepository.save(address);
        businessOwner.setPassword(passwordEncoder.encode(businessOwner.getPassword()));
        businessOwner.setRole("user");
        userRepository.save(businessOwner);

        business = new Business("Test Business", "", address, "Retail Trade", businessOwner.getId());
        businessRepository.save(business);

        product = new Product("TESTPRODUCT", "Test Product", "", "Manufacturer", null, business.getId());
        productRepository.save(product);
    }

     //AC1
     @Given("A product has no images")
     public void aProductHasNoImages() {
         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();
         Assertions.assertEquals(0, retrievedProduct.getImages().size());
     }

     @When("I upload an image")
     public void iUploadAnImage() {

         AddProductImageDTO dto = new AddProductImageDTO(
                 business.getId(),
                 product.getId(),
                 new AppUserDetails(businessOwner),
                 testImageFile
         );
         productImageService.addProductImage(dto);
     }

     @Then("The image is saved in the repository on the product")
     public void theImageIsSavedInTheRepositoryOnTheProduct() {
         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();
         Assertions.assertEquals(1, retrievedProduct.getImages().size());
         Image image = retrievedProduct.getImages().get(0);

         //Remove images from system to clean up
         File imageFile = new File(springEnvironment.getMediaFolderPath() + image.getFilename());
         File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image.getThumbnailFilename());
         Assertions.assertTrue(imageFile.delete());
         Assertions.assertTrue(imageThumbnailFile.delete());
     }


     //AC2
     @Then("The uploaded image is the primary image for the product")
     public void theUploadedImageIsThePrimaryImageForTheProduct() {
         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();
         Assertions.assertEquals(1, retrievedProduct.getImages().size());

         Assertions.assertEquals(retrievedProduct.getPrimaryImageId(), retrievedProduct.getImages().get(0).getId());

         //Remove images from system to clean up
         Image image = retrievedProduct.getImages().get(0);
         File imageFile = new File(springEnvironment.getMediaFolderPath() + image.getFilename());
         File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image.getThumbnailFilename());
         Assertions.assertTrue(imageFile.delete());
         Assertions.assertTrue(imageThumbnailFile.delete());
     }

     @Given("A product has at least 2 images")
     public void aProductHasAtLeast2Images() {
         AddProductImageDTO dto = new AddProductImageDTO(
                 business.getId(),
                 product.getId(),
                 new AppUserDetails(businessOwner),
                 testImageFile);
         //Image 1
         productImageService.addProductImage(dto);
         //Image 2
         productImageService.addProductImage(dto);

         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();
         Assertions.assertEquals(2, retrievedProduct.getImages().size());
         product = retrievedProduct;
     }

     @When("I change the primary image from the first image to the second image")
     public void iChangeThePrimaryImageFromTheFirstImageToTheSecondImage() {
         SetPrimaryProductImageDTO dto = new SetPrimaryProductImageDTO(
                 business.getId(),
                 product.getId(),
                 product.getImages().get(1).getId(),
                 new AppUserDetails(businessOwner));

         productImageService.setPrimaryImage(dto);
     }

     @Then("The primary image for that product is the second image")
     public void thePrimaryImageForThatProductIsTheSecondImage() {
         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();

         Assertions.assertEquals(product.getImages().get(1).getId(), retrievedProduct.getPrimaryImageId());


         //Remove images from system to clean up
         Image image1 = product.getImages().get(0);
         File image1File = new File(springEnvironment.getMediaFolderPath() + image1.getFilename());
         File image1ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image1.getThumbnailFilename());
         Assertions.assertTrue(image1File.delete());
         Assertions.assertTrue(image1ThumbnailFile.delete());
         Image image2 = product.getImages().get(1);
         File image2File = new File(springEnvironment.getMediaFolderPath() + image2.getFilename());
         File image2ThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image2.getThumbnailFilename());
         Assertions.assertTrue(image2File.delete());
         Assertions.assertTrue(image2ThumbnailFile.delete());
     }

     //AC3
     @Then("The uploaded image has a thumbnail")
     public void theUploadedImageHasAThumbnail() {
         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();
         Assertions.assertEquals(1, retrievedProduct.getImages().size());

         Image image = retrievedProduct.getImages().get(0);
         Assertions.assertNotNull(image.getThumbnailFilename());

         //Remove images from system to clean up
         File imageFile = new File(springEnvironment.getMediaFolderPath() + image.getFilename());
         File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + image.getThumbnailFilename());
         Assertions.assertTrue(imageFile.delete());
         Assertions.assertTrue(imageThumbnailFile.delete());
     }

     //AC4
     @Given("A product has an image")
     public void aProductHasAnImage() {
         AddProductImageDTO dto = new AddProductImageDTO(
                 business.getId(),
                 product.getId(),
                 new AppUserDetails(businessOwner),
                 testImageFile
         );
         productImageService.addProductImage(dto);

         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();
         Assertions.assertEquals(1, retrievedProduct.getImages().size());
         product = retrievedProduct;
     }

     @When("I delete an image")
     public void iDeleteAnImage() {
         Image image = product.getImages().get(0);
         deletedImage = image;
         DeleteProductImageDTO dto = new DeleteProductImageDTO(
                 business.getId(),
                 product.getId(),
                 image.getId(),
                 new AppUserDetails(businessOwner));

         productImageService.deleteImage(dto);
     }

     @Then("The product no longer has that image as one of it's images")
     public void theProductNoLongerHasThatImageAsOneOfItSImages() {
         Optional<Product> optionalProduct = productRepository.findByIdAndBusinessId(product.getId(), business.getId());
         Assertions.assertTrue(optionalProduct.isPresent());
         Product retrievedProduct = optionalProduct.get();
         Assertions.assertEquals(0, retrievedProduct.getImages().size());
     }

     @Then("The image is no longer saved")
     public void theImageIsNoLongerSaved() {
         File imageFile = new File(springEnvironment.getMediaFolderPath() + deletedImage.getFilename());
         File imageThumbnailFile = new File(springEnvironment.getMediaFolderPath() + deletedImage.getThumbnailFilename());
         Assertions.assertFalse(imageFile.exists());
         Assertions.assertFalse(imageThumbnailFile.exists());
     }
}
