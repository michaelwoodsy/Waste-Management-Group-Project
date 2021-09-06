package org.seng302.project.web_layer.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.service_layer.dto.contact.PostContactDTO;
import org.seng302.project.service_layer.service.EmailService;
import org.seng302.project.web_layer.authentication.AppUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ContactControllerTest extends AbstractInitializer {

    @Autowired
    private MockMvc mockMvc;
    private PostContactDTO validPostContactDTO;
    private String validPostContactBody;

    @MockBean
    private EmailService emailService;

    @BeforeEach
    void setup() throws JsonProcessingException {
        // create a valid request body
        validPostContactDTO = new PostContactDTO("t.rizzi@gmail.com", "Hello!");
        validPostContactBody = getJsonString(validPostContactDTO);

        // mock the emailService sendEmail method
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());
    }

    /**
     * Maps a PostContactDTO to its equivalent json string.
     *
     * @param dto DTO to convert.
     * @return Converted DTO in json as a string.
     */
    private String getJsonString(PostContactDTO dto) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(dto);
    }

    /**
     * Tests contactResale method returns a 201 response when a request is made by an unauthorised user.
     */
    @Test
    void contactResale_unauthorisedUser_201() throws Exception {
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPostContactBody))
                .andExpect(status().isCreated());
    }

    /**
     * Tests contactResale method returns a 201 response when a request is made by an authorised user.
     */
    @Test
    void contactResale_authorisedUser_201() throws Exception {
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPostContactBody)
                .with(user(new AppUserDetails(getTestUser()))))
                .andExpect(status().isCreated());
    }

    /**
     * Tests contactResale method returns a 201 response when a request is made with a valid email.
     *
     * @param email Email to make the request with.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "t.rizzi@icloud.com",
            "myrtle.t@gmail.com",
            "a@b.co.nz",
            "tom@email.org"
    })
    void contactResale_validEmail_201(String email) throws Exception {
        String body = getJsonString(new PostContactDTO(email, "message"));
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    /**
     * Tests contactResale method returns a 400 response when a request is made with a invalid email.
     *
     * @param email Email to make the request with.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "t.rizziicloud.com",
            "myrtle.t@gmail.",
            "@b.co.nz",
            "tom@org"
    })
    void contactResale_invalidEmail_400(String email) throws Exception {
        String body = getJsonString(new PostContactDTO(email, "message"));
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests contactResale method returns a 201 response when a request is made with a valid message.
     *
     * @param message Message to make the request with.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "Hi, what do you do",
            "Hello",
            " "
    })
    void contactResale_validMessage_201(String message) throws Exception {
        String body = getJsonString(new PostContactDTO("email@gmail.com", message));
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());
    }

    /**
     * Tests contactResale method returns a 400 response when a request is made with a invalid message.
     *
     * @param message Message to make the request with.
     */
    @ParameterizedTest
    @NullAndEmptySource
    void contactResale_invalidMessage_400(String message) throws Exception {
        String body = getJsonString(new PostContactDTO("email@gmail.com", message));
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests contactResale method calls the EmailService sendEmail method with the correct email.
     */
    @Test
    void contactResale_validRequest_callsEmailServiceWithCorrectEmail() throws Exception {
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPostContactBody))
                .andExpect(status().isCreated());

        // capture arguments passed to emailService
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1))
                .sendEmail(emailCaptor.capture(), anyString(), anyString());

        // check the correct arguments are passed to the method
        Assertions.assertEquals("s302resale@gmail.com", emailCaptor.getValue());
    }

    /**
     * Tests contactResale method calls the EmailService sendEmail method with thw correct message.
     */
    @Test
    void contactResale_validRequest_callsEmailServiceWithCorrectMessage() throws Exception {
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPostContactBody))
                .andExpect(status().isCreated());

        // capture arguments passed to emailService
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(emailService, times(1))
                .sendEmail(anyString(), anyString(), messageCaptor.capture());

        // check the message contains both the users email and message
        // the users email needs to be in the message so we know who it's from
        Assertions.assertTrue(messageCaptor.getValue().contains(validPostContactDTO.getMessage()));
        Assertions.assertTrue(messageCaptor.getValue().contains(validPostContactDTO.getEmail()));
    }

    /**
     * Tests contactResale method doesn't call the emailService when the request is invalid.
     */
    @Test
    void contactResale_invalidRequest_doesntCallEmailService() throws Exception {
        String body = getJsonString(new PostContactDTO("", null));
        mockMvc.perform(post("/contact")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());

        // verify the method wasn't called
        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());
    }

}
