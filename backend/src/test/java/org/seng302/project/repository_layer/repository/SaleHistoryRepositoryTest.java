package org.seng302.project.repository_layer.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.Sale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
class SaleHistoryRepositoryTest extends AbstractInitializer {

    @Autowired
    private SaleHistoryRepository saleHistoryRepository;

    /**
     * Basic test to show that the entity can be saved.
     */
    @Test
    void saleHistory_savingEntity_noError() {
        Sale sale = new Sale(getSaleListings().get(0));
        assertDoesNotThrow(() -> saleHistoryRepository.save(sale));
    }

    /**
     * Basic test to show that the entity can be saved and is actually stored.
     */
    @Test
    void saleHistory_savingEntity_isSaved() {
        Sale sale = new Sale(getSaleListings().get(0));
        saleHistoryRepository.save(sale);
        Optional<Sale> foundSale = saleHistoryRepository.findById(sale.getSaleId());
        Assertions.assertTrue(foundSale.isPresent());
    }
}
