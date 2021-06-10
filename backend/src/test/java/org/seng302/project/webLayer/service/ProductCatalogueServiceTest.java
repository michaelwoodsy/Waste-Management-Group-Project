package org.seng302.project.webLayer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.Image;
import org.seng302.project.repositoryLayer.model.Product;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.InventoryItemRepository;
import org.seng302.project.repositoryLayer.repository.ProductRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.exceptions.NoBusinessExistsException;
import org.seng302.project.serviceLayer.exceptions.businessAdministrator.ForbiddenAdministratorActionException;
import org.seng302.project.serviceLayer.service.ProductCatalogueService;
import org.seng302.project.webLayer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;


import static org.mockito.BDDMockito.given;

@SpringBootTest
class ProductCatalogueServiceTest {

    @Autowired
    private ProductCatalogueService productCatalogueService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BusinessRepository businessRepository;
    @MockBean
    private ProductRepository productRepository;
    @MockBean
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User user;
    private User owner;

    @BeforeEach
    public void setup() {
        //Mock a regular user
        user = new User("John", "Smith", "Bob", "Jonny",
                "Likes long walks on the beach", "johnxyz@gmail.com", "1999-04-27",
                "+64 3 555 0129", null, "1337-H%nt3r2");
        user.setId(1);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        given(userRepository.findByEmail("johnxyz@gmail.com")).willReturn(List.of(user));
        given(userRepository.findById(1)).willReturn(Optional.of(user));

        //Mock a business owner
        owner = new User("Jane", "Smith", "Rose", "Jonny",
                "Likes long walks on the beach", "jane111@gmail.com", "1999-04-27",
                "+64 3 555 0120", null, "1337-H%nt3r2");
        owner.setId(2);
        owner.setPassword(passwordEncoder.encode(owner.getPassword()));
        given(userRepository.findByEmail("jane111@gmail.com")).willReturn(List.of(owner));
        given(userRepository.findById(2)).willReturn(Optional.of(owner));

        //Mock a regular business
        Business testBusiness = new Business("Business", "A Business", null, "Retail",
                owner.getId());
        testBusiness.setId(1);
        given(businessRepository.findByName("Business")).willReturn(List.of(testBusiness));
        given(businessRepository.findById(1)).willReturn(Optional.of(testBusiness));

        Product product1 = new Product("CARROT", "Carrot", "Orange carrot",
                "The Farm", 0.20, 1);
        Image carrotImage = new Image("carrot.jpg", "carrot_thumbnail.jpg");
        Image carrotImage2 = new Image("carrot2.jpg", "carrot2_thumbnail.jpg");
        product1.addImage(carrotImage);
        product1.addImage(carrotImage2);
        Product product2 = new Product("POTATO", "Potato", "Big Potato",
                "The Farm", 0.40, 1);
        Image potatoImage = new Image("potato.jpg", "potato_thumbnail.jpg");
        product2.addImage(potatoImage);
        given(productRepository.findAllByBusinessId(1)).willReturn(List.of(product1, product2));

    }

    /**
     * Tries to get the business products as a logged in normal user.
     * Expects a ForbiddenAdministratorActionException
     */
    @Test
    void getProducts_notAdmin_forbiddenException() {
        Assertions.assertThrows(ForbiddenAdministratorActionException.class,
                () -> productCatalogueService.getBusinessesProducts(1, new AppUserDetails(user)));
    }


    /**
     * Tries to get the business products as the business owner.
     * Expects a list of 2 products
     */
    @Test
    void getProducts_success() {
        List<Product> returnedProducts = productCatalogueService
                .getBusinessesProducts(1, new AppUserDetails(owner));
        Assertions.assertEquals(2, returnedProducts.size());
        Assertions.assertEquals("CARROT", returnedProducts.get(0).getId());
        Assertions.assertEquals("POTATO", returnedProducts.get(1).getId());
    }

    /**
     * Tries to get the business products with images
     * Expects the first product to have 2 images
     * and the second to have 1 image
     */
    @Test
    void getProducts_withImages_success() {
        List<Product> returnedProducts = productCatalogueService
                .getBusinessesProducts(1, new AppUserDetails(owner));
        Assertions.assertEquals(2, returnedProducts.size());

        Product firstProduct = returnedProducts.get(0);
        Assertions.assertEquals(2, firstProduct.getImages().size());
        Assertions.assertEquals("carrot.jpg", firstProduct.getImages().get(0).getFilename());
        Assertions.assertEquals("carrot2.jpg", firstProduct.getImages().get(1).getFilename());

        Product secondProduct = returnedProducts.get(1);
        Assertions.assertEquals("potato.jpg", secondProduct.getImages().get(0).getFilename());

    }

    /**
     * Tries to get products from a nonexistent business.
     * Expects a NoBusinessExistsException
     */
    @Test
    void getProducts_nonExistentBusiness_noBusinessExistsException() {
        given(businessRepository.findById(4)).willReturn(Optional.empty());

        Assertions.assertThrows(NoBusinessExistsException.class,
                () -> productCatalogueService.getBusinessesProducts(4, new AppUserDetails(user)));
    }


    //Add product without id
    //MissingProductIdException

    //Add product without name
    //MissingProductNameException

    //Add product with existing id
    //ProductIdAlreadyExistsException

    //Add product with invalid id
    //InvalidProductIdCharactersException

    //Successful product creation


}
