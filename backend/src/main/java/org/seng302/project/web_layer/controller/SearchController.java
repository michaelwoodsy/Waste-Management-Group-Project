package org.seng302.project.web_layer.controller;


import org.seng302.project.repository_layer.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller with all our search functionality.
 */
@RestController
public class SearchController {

    private static final Logger logger = LoggerFactory.getLogger(SearchController.class.getName());
    private final UserRepository userRepository;

    @Autowired
    public SearchController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


}