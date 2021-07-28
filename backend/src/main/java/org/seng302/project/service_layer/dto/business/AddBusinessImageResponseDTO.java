package org.seng302.project.service_layer.dto.business;

import lombok.Data;

@Data
public class AddBusinessImageResponseDTO {

    private Integer imageId;

    public AddBusinessImageResponseDTO(Integer id) {
        this.imageId = id;
    }
}
