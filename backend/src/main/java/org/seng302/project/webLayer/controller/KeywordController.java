package org.seng302.project.webLayer.controller;

import org.seng302.project.serviceLayer.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling request related to Keywords
 */
@RestController
public class KeywordController {

    private final KeywordService keywordService;

    @Autowired
    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

}
