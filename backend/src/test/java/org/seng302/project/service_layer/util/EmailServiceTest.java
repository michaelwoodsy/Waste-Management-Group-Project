package org.seng302.project.service_layer.util;

import org.junit.jupiter.api.Test;
import org.seng302.project.service_layer.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EmailServiceTest {

    @Autowired
    private EmailServiceImpl emailServiceImpl;

    @Test
    void sanityCheck() {
        emailServiceImpl.sendSimpleMessage("t.rizzi@icloud.com", "Hello", "hi");
    }

}
