package org.seng302.project.service_layer.dto.user;

import lombok.Data;

@Data
public class AddUserImageResponseDTO {

    private Integer imageId;

    public AddUserImageResponseDTO(Integer id) {
        this.imageId = id;
    }

}
