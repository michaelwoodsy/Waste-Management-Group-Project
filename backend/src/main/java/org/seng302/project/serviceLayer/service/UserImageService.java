package org.seng302.project.serviceLayer.service;

import org.seng302.project.repositoryLayer.repository.ImageRepository;
import org.seng302.project.repositoryLayer.repository.UserRepository;
import org.seng302.project.serviceLayer.dto.user.AddUserImageDTO;
import org.seng302.project.serviceLayer.dto.user.AddUserImageResponseDTO;
import org.seng302.project.serviceLayer.util.ImageUtil;
import org.seng302.project.serviceLayer.util.SpringEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserImageService {

    private static final Logger logger = LoggerFactory.getLogger(ProductImageService.class.getName());

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ImageUtil imageUtil;
    private final SpringEnvironment springEnvironment;

    @Autowired
    public UserImageService(UserRepository userRepository,
                            ImageRepository imageRepository,
                            ImageUtil imageUtil,
                            SpringEnvironment springEnvironment) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.imageUtil = imageUtil;
        this.springEnvironment = springEnvironment;
    }

    public AddUserImageResponseDTO addUserImage(AddUserImageDTO dto) {
        return null;
    }
}
