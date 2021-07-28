package org.seng302.project.web_layer.controller;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.service_layer.dto.product.AddProductDTO;
import org.seng302.project.service_layer.dto.product.ProductSearchDTO;
import org.seng302.project.service_layer.exceptions.*;
import org.seng302.project.service_layer.exceptions.business.BusinessNotFoundException;
import org.seng302.project.service_layer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.service_layer.service.ProductCatalogueService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ProductCatalogueController class.
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class ProductCatalogueControllerTest {

    private User user;
    private User owner;
    private final Integer businessId = 1;
    private final String productId = "BEANS";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductCatalogueService productCatalogueService;


    @BeforeEach
    public void initialise() {
        // Create the users
        Address userAddress = new Address(null, null, null, null, "New Zealand", null);
        user = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach",  "johnxyz@gmail.com", "1999-04-27",
                "+64 3 555 0129", userAddress, "1337-H%nt3r2");

        Address ownerAddress = new Address(null, null, null, null, "New Zealand", null);
        owner = new User("Jane", "Smith", "Rose", "Jonny",
                "Likes long walks on the beach", "jane111@gmail.com", "1999-04-27",
                "+64 3 555 0120", ownerAddress, "1337-H%nt3r2");

    }

    /**
     * Tries to get the business products as a logged in normal user.
     * Expects a 403 forbidden response.
     */
    @Test
    void getProducts_randomUser_403Response() throws Exception {
        Mockito.when(productCatalogueService.getBusinessesProducts(Mockito.any(Integer.class),
                Mockito.any(AppUserDetails.class)))
                .thenThrow(new ForbiddenAdministratorActionException(businessId));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{id}/products", businessId)
                .with(user(new AppUserDetails(user)));

        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    /**
     * Tries to get the business products as a logged in owner.
     * Expects a 200 OK response
     */
    @Test
    void getProducts_200Response() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{id}/products", businessId)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request).andExpect(status().isOk());
    }

    /**
     * Tries to get products from a nonexistent business.
     * Expects a 406 response.
     */
    @Test
    void getProducts_nonexistentBusiness_406Response() throws Exception {

        Mockito.when(productCatalogueService.getBusinessesProducts(Mockito.any(Integer.class),
                Mockito.any(AppUserDetails.class)))
                .thenThrow(new BusinessNotFoundException(businessId));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/businesses/{id}/products", businessId + 999999)
                .with(user(new AppUserDetails(user)));

        mockMvc.perform(request).andExpect(status().isNotAcceptable());
    }


    /**
     * Tries to get business products when not logged in
     * Expects a 401 response.
     *
     * @throws Exception possible exception from using MockMvc
     */
    @Test
    void getProducts_notLoggedIn_401Response() throws Exception {
        mockMvc.perform(get("/business/{id}/products", businessId))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tries creating a product without a product id.
     * Expect a 400 response and a message from the DTO
     */
    @Test
    void addProduct_noProductId_400Response() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "");
        testProduct.put("name", "Sarah's cookies");
        testProduct.put("description", "20pk of delicious home baked cookies");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();

        //Sometimes the regex is checked first, sometimes the non-empty property is checked first
        Assertions.assertTrue(returnedExceptionString.equals(
                "MethodArgumentNotValidException: Product id is a mandatory field") ||
                returnedExceptionString.equals("MethodArgumentNotValidException: This productId contains invalid characters. " +
                        "Acceptable characters are letters, numbers and dashes."));

    }


    /**
     * Tries creating a product without a product name.
     * Expect a 400 response and a message from the DTO
     */
    @Test
    void addProduct_noProductName_400Response() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "S-COOKIES");
        testProduct.put("name", "");
        testProduct.put("description", "20pk of delicious home baked cookies");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Product name is a mandatory field", returnedExceptionString);
    }

    /**
     * Tries creating a product with an existing product id.
     * Expect a 400 response.
     */
    @Test
    void addProduct_existingId_400Response() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "p1");
        testProduct.put("name", "My first product");

        Mockito.doThrow(new ProductIdAlreadyExistsException()).when(productCatalogueService)
                .newProduct(Mockito.any(AddProductDTO.class));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /**
     * Tries creating a product where the product id has invalid characters.
     * Expect a 400 response and message from the DTO
     */
    @Test
    void addProduct_invalidProductId_400Response() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "Sarah's Cookies");
        testProduct.put("name", "Sarah's Cookies");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: This productId contains invalid characters. " +
                "Acceptable characters are letters, numbers and dashes.", returnedExceptionString);
    }

    /**
     * Tests successful creation of a product through the POST method
     */
    @Test
    void addProduct_success_201Response() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "S-COOKIES");
        testProduct.put("name", "Sarah's cookies");
        testProduct.put("description", "20pk of delicious home baked cookies");
        testProduct.put("manufacturer", "Sarah");

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    /**
     * Tests successful creation of a product through the POST method
     */
    @Test
    void addProduct_negativePrice_400Response() throws Exception {
        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "S-COOKIES");
        testProduct.put("name", "Sarah's cookies");
        testProduct.put("description", "Free cookies with cash!");
        testProduct.put("recommendedRetailPrice", -2.00);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/businesses/{businessId}/products", businessId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Price cannot be negative", returnedExceptionString);

    }

    /**
     * Tries editing a product and giving it an invalid id.
     * Expect a 400 response and a message from the DTO
     */
    @Test
    void editProduct_invalidId_400Response() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "invalid id+/");
        testProduct.put("name", "Not Fresh Water");

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, productId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: This productId contains invalid characters. " +
                "Acceptable characters are letters, numbers and dashes.", returnedExceptionString);
    }


    /**
     * Tries editing a product and giving it an empty id.
     * Expect a 400 response and a message from the DTO
     */
    @Test
    void editProduct_emptyId_400Response() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "");
        testProduct.put("name", "Not Fresh Water");

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, productId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();

        //Sometimes the regex is checked first, sometimes the non-empty property is checked first
        Assertions.assertTrue(returnedExceptionString.equals(
                "MethodArgumentNotValidException: Product id is a mandatory field") ||
                returnedExceptionString.equals("MethodArgumentNotValidException: This productId contains invalid characters. " +
                        "Acceptable characters are letters, numbers and dashes."));
    }

    /**
     * Tries editing a product and giving it an empty name.
     * Expect a 400 response and a message from the DTO
     */
    @Test
    void editProduct_emptyName_400Response() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "Empty-name");
        testProduct.put("name", "");

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, productId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Product name is a mandatory field", returnedExceptionString);
    }


    /**
     * Tests editing a product, without editing its id
     * Expect 200 response
     */
    @Test
    void editProduct_notChangingId_200Response() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("id", productId);
        testProduct.put("name", "Different Kind of Water");
        testProduct.put("description", "Water from your local sewer");
        testProduct.put("manufacturer", "Sewerage department");
        testProduct.put("recommendedRetailPrice", 3.00);

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, productId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        this.mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests editing a product, setting a negative price
     * Expect 400 response and message from the DTO
     */
    @Test
    void editProduct_negativePrice_400Response() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("id", productId);
        testProduct.put("name", "Why pay us when we can pay you!");
        testProduct.put("recommendedRetailPrice", -3.00);

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, productId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        MvcResult response = mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String returnedExceptionString = response.getResponse().getContentAsString();
        Assertions.assertEquals("MethodArgumentNotValidException: Price cannot be negative", returnedExceptionString);

    }


    /**
     * Tests editing a product, also its Id
     * Expects a 200 response
     */
    @Test
    void editProduct_changeId_200Response() throws Exception {

        JSONObject testProduct = new JSONObject();
        testProduct.put("id", "Sprite-1.5L");
        testProduct.put("name", "Sprite Lemon flavour - 1.5L");
        testProduct.put("description", "Your favourite beverage in lemon");
        testProduct.put("manufacturer", "Coca Cola");
        testProduct.put("recommendedRetailPrice", 0.99);

        RequestBuilder putProductRequest = MockMvcRequestBuilders
                .put("/businesses/{businessId}/products/{productId}", businessId, productId)
                .content(testProduct.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(putProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());

    }


    /**
     * Tests that a successful product search gives a 200 response
     */
    @Test
    void searchProducts_success200() throws Exception {
        RequestBuilder searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery=beans&matchingId=true&matchingName=true&matchingDescription=true",
                        businessId)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    /**
     * Tests that a product search without being logged in gives a 401 response
     */
    @Test
    void searchProducts_notLoggedIn_401() throws Exception {

        RequestBuilder searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery=beans&matchingId=true&matchingName=true&matchingDescription=true",
                        businessId)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }


    /**
     * Tests that a product search from someone not an admin gives a 403 response
     */
    @Test
    void searchProducts_notAdmin_403() throws Exception {
        when(productCatalogueService.searchProducts(
                any(Integer.class), any(String.class), any(ProductSearchDTO.class), any(AppUserDetails.class)))
                .thenThrow(new ForbiddenAdministratorActionException(businessId));

        RequestBuilder searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery=beans&matchingId=true&matchingName=true&matchingDescription=true",
                        businessId)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(user)));

        mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }


    /**
     * Tests that a product search of non-existent business gives a 406 response
     */
    @Test
    void searchProducts_nonExistentBusiness_406() throws Exception {
        when(productCatalogueService.searchProducts(
                any(Integer.class), any(String.class), any(ProductSearchDTO.class), any(AppUserDetails.class)))
                .thenThrow(new NotAcceptableException("There is no business that exists with the id '500'"));

        RequestBuilder searchProductRequest = MockMvcRequestBuilders
                .get("/businesses/{businessId}/products/search?searchQuery=beans&matchingId=true&matchingName=true&matchingDescription=true",
                        500)
                .accept(MediaType.APPLICATION_JSON)
                .with(user(new AppUserDetails(owner)));

        mockMvc.perform(searchProductRequest)
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }
}


