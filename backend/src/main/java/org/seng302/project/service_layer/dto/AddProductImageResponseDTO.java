package org.seng302.project.service_layer.dto;

import lombok.Data;

@Data
public class AddProductImageResponseDTO {

    private Integer imageId;

    public AddProductImageResponseDTO(Integer id) {
        this.imageId = id;
    }

}
