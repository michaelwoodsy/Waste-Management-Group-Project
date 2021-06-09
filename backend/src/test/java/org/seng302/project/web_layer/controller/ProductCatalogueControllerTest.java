package org.seng302.project.web_layer.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.AddressRepository;
import org.seng302.project.repository_layer.repository.BusinessRepository;
import org.seng302.project.repository_layer.repository.ProductRepository;
import org.seng302.project.repository_layer.repository.UserRepository;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ProductCatalogueController class.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
public class ProductCatalogueControllerTest {

    private User user;
    private User owner;
    private Integer businessId;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ProductCatalogueController productCatalogueController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BusinessRepository businessRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    private Business testBusiness;

    /**
     * Creates the user if it's not already created.
     * If it is already created, the user is returned.
     *
     * @return User
     */
    private User createUser(User wantedUser) {
        if (userRepository.findByEmail(wantedUser.getEmail()).size() > 0) {
            // Already exists, return it
            return (userRepository.findByEmail(wantedUser.getEmail()).get(0));
        } else {
            // User doesn't exist, save it to repository
            wantedUser.setPassword(passwordEncoder.encode(wantedUser.getPassword()));
            addressRepository.save(wantedUser.getHomeAddress());
            userRepository.save(wantedUser);
            return wantedUser;
        }
    }

    @BeforeEach
    public void initialise() {
        // Create the users

        Address userAddress = new Address(null, null, null, null, "New Zealand", null);
        user = createUser(new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "jane111@gmail.com", "1999-04-27",
                "+64 3 555 0129", userAddress, "1337-H%nt3r2"));

        Address ownerAddress = new Address(null, null, null, null, "New Zealand", null);
        owner = createUser(new User("Jane", "Smith", "Rose", "Jonny",
                "Likes long walks on the beach", "johnxyz@gmail.com", "1999-04-27",
                "+64 3 555 0120", ownerAddress, "1337-H%nt3r2"));

        // Create the business
        Address businessAddress = new Address(null, null, null, null, "New Zealand", null);
        testBusiness = new Business("Business", "A Business", businessAddress, "Retail",
                owner.getId());

        addressRepository.save(businessAddress);
        businessRepository.save(testBusiness);
        businessId = testBusiness.getId();

        // Create a product
        Product product = new Product("p1", "Watties Beans", "beans in a can", "Watties", 2.00,
                businessId);
        productRepository.save(product);

        // Create another product
        Product product2 = new Product("p2", "Fresh Water", "Water straight from a spring", "Rutgers", 10.00,
                businessId);
        productRepository.save(product2);
    }

    /**
     * Tries to get the business products as a logged in normal user.
     * Expects a 403 forbidden response.
     */
    @Test
    void testRandomUserCantAccess() {
        assertThrows(ForbiddenAdministratorActionException.class,
                () -> productCatalogueController.getBusinessesProducts(businessId, new AppUserDetails(user)));
    }

    /**
     * Tries to get the business products as a logged in owner.
     * Expects a 200 OK response, and product present.
     */
    @Test
    void testAdministratorCanGetProducts() {
        List<Product> products =
                productCatalogueController.getBusinessesProducts(businessId, new AppUserDetails(owner));
        assertEquals(2, products.size());
        assertEquals("Watties Beans", products.get(0).getName());
    }

    /**
     * Tries to get a non existent business.
     * Expects a 406 response.
     */
    @Test
    void testNonExistentBusiness() {
        assertThrows(NoBusinessExistsException.class,
                () -> productCatalogueController.getBusinessesProducts(businessId + 999999, new AppUserDetails(user)));
    }

    /**
     * Tries to get a non existent business.
     * Expects a 406 response.
     *
     * @throws Exception possible exception from using MockMvc
     */
    @Test
    void testUnauthorised() throws Exception {
        mockMvc.perform(get("/business/{id}/products", businessId))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tries creating a product without a product id.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingProductWithoutId() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("name", "Sarah's cookies");
        testProduct.put("description", "20pk of delicious home baked cookies");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mockMvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new MissingProductIdException().getMessage(), returnedExceptionString);

    }


    /**
     * Tries creating a product without a product name.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingProductWithoutName() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "S-COOKIES");
        testProduct.put("name", "");
        testProduct.put("description", "20pk of delicious home baked cookies");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mockMvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new MissingProductNameException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a product with an existing product id.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingProductExistingId() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "p1");
        testProduct.put("name", "My first product");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mockMvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new ProductIdAlreadyExistsException().getMessage(), returnedExceptionString);
    }

    /**
     * Tries creating a product where the product id has invalid characters.
     * Expect a 400 response.
     */
    @Test
    void tryCreatingInvalidProductId() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "Sarah's Cookies");
        testProduct.put("name", "Sarah's Cookies");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mockMvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = postUserResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidProductIdCharactersException().getMessage(), returnedExceptionString);
    }

    /**
     * Tests successful creation of a product through the POST method
     */
    @Test
    void createAndRetrieveProduct() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "S-COOKIES");
        testProduct.put("name", "Sarah's cookies");
        testProduct.put("description", "20pk of delicious home baked cookies");
        testProduct.put("manufacturer", "Sarah");

        RequestBuilder postUserRequest = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult postUserResponse = this.mockMvc.perform(postUserRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated()) // We expect a 201 response
                .andReturn();

        Optional<Product> retrievedProductOptions = productRepository.findByIdAndBusinessId("S-COOKIES", businessId);
        Assertions.assertTrue(retrievedProductOptions.isPresent());

        Product retrievedProduct = retrievedProductOptions.get();

        Assertions.assertEquals("Sarah's cookies", retrievedProduct.getName());
        Assertions.assertEquals("20pk of delicious home baked cookies", retrievedProduct.getDescription());
        Assertions.assertEquals("Sarah", retrievedProduct.getManufacturer());
        Assertions.assertNull(retrievedProduct.getRecommendedRetailPrice());
        Assertions.assertEquals(businessId, retrievedProduct.getBusinessId());
    }

    /**
     * Tests editing a product and giving it an invalid id
     */
    @Test
    void editProductInvalidId() throws Exception {

        Optional<Product> retrievedProductOptions = productRepository.findByIdAndBusinessId("p2", businessId);
        Assertions.assertTrue(retrievedProductOptions.isPresent());

        Product retrievedProduct = retrievedProductOptions.get();


        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "invalid id+/");
        testProduct.put("name", "Not Fresh Water");

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, retrievedProduct.getId())
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        MvcResult putProductResponse = this.mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // We expect a 400 response
                .andReturn();

        String returnedExceptionString = putProductResponse.getResponse().getContentAsString();
        Assertions.assertEquals(new InvalidProductIdCharactersException().getMessage(), returnedExceptionString);
    }

    /**
     * Tests editing a product and giving it nothing to change
     */
    @Test
    void editProductNoChanges() throws Exception {

        Optional<Product> retrievedProductOptions = productRepository.findByIdAndBusinessId("p2", businessId);
        Assertions.assertTrue(retrievedProductOptions.isPresent());

        Product retrievedProduct = retrievedProductOptions.get();


        JSONObject testProduct = new JSONObject();

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, retrievedProduct.getId())
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        this.mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        //Get edited product
        Optional<Product> newRetrievedProductOptions = productRepository.findByIdAndBusinessId("p2", businessId);
        Assertions.assertTrue(newRetrievedProductOptions.isPresent());

        Product newRetrievedProduct = newRetrievedProductOptions.get();

        System.out.println(newRetrievedProduct);

        Assertions.assertEquals("p2", newRetrievedProduct.getId());
        Assertions.assertEquals("Fresh Water", newRetrievedProduct.getName());
        Assertions.assertEquals("Water straight from a spring", newRetrievedProduct.getDescription());
        Assertions.assertEquals("Rutgers", newRetrievedProduct.getManufacturer());
        Assertions.assertEquals(10.00, newRetrievedProduct.getRecommendedRetailPrice());
    }

    /**
     * Tests editing a product, without editing its id
     */
    @Test
    void editProduct() throws Exception {

        Optional<Product> retrievedProductOptions = productRepository.findByIdAndBusinessId("p2", businessId);
        Assertions.assertTrue(retrievedProductOptions.isPresent());

        Product retrievedProduct = retrievedProductOptions.get();


        JSONObject testProduct = new JSONObject();
        testProduct.put("name", "Different Kind of Water");
        testProduct.put("description", "Water from your local sewer");
        testProduct.put("manufacturer", "Sewerage department");
        testProduct.put("recommendedRetailPrice", 3.00);

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, retrievedProduct.getId())
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        this.mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        //Get edited product
        Optional<Product> newRetrievedProductOptions = productRepository.findByIdAndBusinessId("p2", businessId);
        Assertions.assertTrue(newRetrievedProductOptions.isPresent());


        Product newRetrievedProduct = newRetrievedProductOptions.get();

        System.out.println(newRetrievedProduct);

        Assertions.assertEquals("p2", newRetrievedProduct.getId());
        Assertions.assertEquals("Different Kind of Water", newRetrievedProduct.getName());
        Assertions.assertEquals("Water from your local sewer", newRetrievedProduct.getDescription());
        Assertions.assertEquals("Sewerage department", newRetrievedProduct.getManufacturer());
        Assertions.assertEquals(3.00, newRetrievedProduct.getRecommendedRetailPrice());
    }

    /**
     * Tests editing a product, also its Id
     */
    @Test
    void editProductAndId() throws Exception {

        Optional<Product> retrievedProductOptions = productRepository.findByIdAndBusinessId("p2", businessId);
        Assertions.assertTrue(retrievedProductOptions.isPresent());

        Product retrievedProduct = retrievedProductOptions.get();


        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "Sprite-1.5L");
        testProduct.put("name", "Sprite Lemon flavour - 1.5L");
        testProduct.put("description", "Your favourite beverage in lemon");
        testProduct.put("manufacturer", "Coca Cola");
        testProduct.put("recommendedRetailPrice", 0.99);

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, retrievedProduct.getId())
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(httpBasic("johnxyz@gmail.com", "1337-H%nt3r2"));

        this.mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk()) // We expect a 200 response
                .andReturn();

        //Check if old product is removed (Because Id changed, original product has to be removed)
        Optional<Product> newRetrievedProductOptions = productRepository.findByIdAndBusinessId("p2", businessId);
        Assertions.assertTrue(newRetrievedProductOptions.isEmpty());

        //Get edited product
        newRetrievedProductOptions = productRepository.findByIdAndBusinessId("Sprite-1.5L", businessId);
        Assertions.assertTrue(newRetrievedProductOptions.isPresent());


        Product newRetrievedProduct = newRetrievedProductOptions.get();

        System.out.println(newRetrievedProduct);

        Assertions.assertEquals("Sprite-1.5L", newRetrievedProduct.getId());
        Assertions.assertEquals("Sprite Lemon flavour - 1.5L", newRetrievedProduct.getName());
        Assertions.assertEquals("Your favourite beverage in lemon", newRetrievedProduct.getDescription());
        Assertions.assertEquals("Coca Cola", newRetrievedProduct.getManufacturer());
        Assertions.assertEquals(0.99, newRetrievedProduct.getRecommendedRetailPrice());
    }
}


