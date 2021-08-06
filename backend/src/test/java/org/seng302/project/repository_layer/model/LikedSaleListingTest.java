package org.seng302.project.repository_layer.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.seng302.project.repository_layer.model.enums.Tag;

class LikedSaleListingTest {

    @ParameterizedTest
    @ValueSource(strings = {"red", "orange", "yellow", "green", "blue", "purple", "none"})
    void checkTag_validTypes_returnsTrue(String name) {
        Assertions.assertTrue(Tag.checkTag(name));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"random", "NotAValidTag"})
    void checkTag_invalidTypes_returnsFalse(String name) {
        Assertions.assertFalse(Tag.checkTag(name));
    }

}
