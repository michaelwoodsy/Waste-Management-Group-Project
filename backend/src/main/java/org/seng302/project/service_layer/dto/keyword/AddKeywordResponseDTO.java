package org.seng302.project.service_layer.dto.keyword;

import lombok.Data;

/**
 * Response DTO containing keyword ID of keyword that has been added to system keyword list.
 */
@Data
public class AddKeywordResponseDTO {

    private Integer keywordId;

    public AddKeywordResponseDTO(Integer keywordId) {
        this.keywordId = keywordId;
    }

}
