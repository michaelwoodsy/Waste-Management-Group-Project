package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.repository.BusinessRepository;
import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageDTO;
import org.seng302.project.serviceLayer.dto.business.AddBusinessImageResponseDTO;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessImageService {

    private static final Logger logger = LoggerFactory.getLogger(BusinessImageService.class.getName());

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ImageRepository imageRepository;
    private final ImageUtil imageUtil;
    private final SpringEnvironment springEnvironment;

    @Autowired
    public BusinessImageService(UserRepository userRepository,
                               BusinessRepository businessRepository,
                               ImageRepository imageRepository,
                               ImageUtil imageUtil,
                               SpringEnvironment springEnvironment) {
        this.userRepository = userRepository;
        this.businessRepository = businessRepository;
        this.imageRepository = imageRepository;
        this.imageUtil = imageUtil;
        this.springEnvironment = springEnvironment;
    }

    public AddBusinessImageResponseDTO addBusinessImage(AddBusinessImageDTO dto) {
        logger.info("Request to add an image for business {}", dto.getBusinessId());

        return null;
    }
}
