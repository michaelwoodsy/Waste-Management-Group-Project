package org.seng302.project.serviceLayer.dto;

import lombok.Data;

@Data
public class AddProductImageResponseDTO {

    private Integer imageId;

    public AddProductImageResponseDTO(Integer id) {
        this.imageId = id;
    }

}
