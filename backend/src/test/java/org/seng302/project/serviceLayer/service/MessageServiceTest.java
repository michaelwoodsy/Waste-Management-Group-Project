package org.seng302.project.serviceLayer.service;

import org.junit.jupiter.api.BeforeEach;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repositoryLayer.model.User;
import org.seng302.project.repositoryLayer.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Test class for performing unit tests for the MessageService class and its methods.
 */
@SpringBootTest
public class MessageServiceTest extends AbstractInitializer {

    @Autowired
    private MessageService messageService;

    @MockBean
    private MessageRepository messageRepository;

    private User testUser;

    /**
     * Initialises entities from AbstractInitializer
     */
    @BeforeEach
    void setup() {
        this.initialise();
        testUser = this.getTestUser();
    }

    //Test success for creating message

    //Test missing text field throws BadRequestException

    //Test nonexistent user throws NotAcceptableException

    //Test nonexistent card throws NotAcceptableException

}
