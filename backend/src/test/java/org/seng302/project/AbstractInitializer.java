package org.seng302.project;

import io.cucumber.java.bs.A;
import org.seng302.project.repositoryLayer.model.Address;
import org.seng302.project.repositoryLayer.model.Business;
import org.seng302.project.repositoryLayer.model.User;

public class AbstractInitializer {

    private User testUser;
    private User testUserBusinessAdmin;
    private Business testBusiness;

    public void initialise() {
        this.initialiseTestUser();
        this.initialiseTestUserBusinessAdmin();
        this.initialiseTestBusiness();
    }

    public void initialiseTestUser() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testUser = new User(
                "John",
                "Smith",
                "Hector",
                null,
                null,
                "john.smith@gmail.com",
                "1995/07/25",
                null,
                address,
                "password");
        testUser.setId(1);
    }

    public void initialiseTestUserBusinessAdmin() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testUser = new User(
                "Jane",
                "Doe",
                "Angela",
                null,
                null,
                "jane.doe@gmail.com",
                "1995/06/21",
                null,
                address,
                "password");
        testUser.setId(2);
    }

    public void initialiseTestBusiness() {
        Address address = new Address(null, null, null, null, "New Zealand", null);
        testBusiness = new Business(
                "Test Business",
                "A test business",
                address,
                "Retail Trade",
                2);
        testBusiness.addAdministrator(testUserBusinessAdmin);
        testBusiness.setId(1);
    }

    public User getTestUser() {
        return this.testUser;
    }

    public User getTestUserBusinessAdmin() {
        return this.testUserBusinessAdmin;
    }

    public Business getTestBusiness() {
        return this.testBusiness;
    }

}
