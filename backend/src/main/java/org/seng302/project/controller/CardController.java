package org.seng302.project.controller;

import net.minidev.json.JSONObject;
import org.seng302.project.exceptions.NoUserExistsException;
import org.seng302.project.exceptions.RequiredFieldsMissingException;
import org.seng302.project.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST controller for handling requests to do with marketplace cards.
 */
@RestController
public class CardController {


}
