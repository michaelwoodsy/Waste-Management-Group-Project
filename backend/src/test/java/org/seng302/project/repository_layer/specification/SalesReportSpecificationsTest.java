package org.seng302.project.repository_layer.specification;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.seng302.project.repository_layer.model.*;
import org.seng302.project.repository_layer.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class SalesReportSpecificationsTest {

    @Autowired
    private BusinessRepository businessRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;
    @Autowired
    private SaleListingRepository saleListingRepository;
    @Autowired
    private SaleHistoryRepository saleHistoryRepository;

    @BeforeEach
    void setup() {
        Business business = new Business("First Business", null, null, "Retail Trade", 1);
        businessRepository.save(business);
        Product product = new Product("TEST-1", "First Product", null, null, 5.00, business.getId());
        productRepository.save(product);
        InventoryItem inventoryItem = new InventoryItem(product, 10, null, null, "2021-01-01", null, null, "2021-12-31");
        inventoryItemRepository.save(inventoryItem);
        SaleListing saleListing1 = new SaleListing(business, inventoryItem, 20.00, "Sold before August", LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListingRepository.save(saleListing1);
        SaleListing saleListing2 = new SaleListing(business, inventoryItem, 5.00, "Sold after August", LocalDateTime.parse("2021-08-25T00:00:00"), 5);
        saleListingRepository.save(saleListing2);
        Sale sale1 = new Sale(saleListing1);
        Sale sale2 = new Sale(saleListing2);
        sale1.setDateSold(LocalDateTime.parse("2021-07-25T00:00:00"));
        sale2.setDateSold(LocalDateTime.parse("2021-09-06T00:00:00"));
        saleHistoryRepository.save(sale1);
        saleHistoryRepository.save(sale2);
    }

    @Test
    void soldBefore_october_returnsBothSales() {
        Specification<Sale> specification = SalesReportSpecifications.soldBefore(LocalDateTime.parse("2021-10-30T00:00:00"));
        List<Sale> result = saleHistoryRepository.findAll(specification);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void soldBefore_august_returnsOneSale() {
        Specification<Sale> specification = SalesReportSpecifications.soldBefore(LocalDateTime.parse("2021-08-30T00:00:00"));
        List<Sale> result = saleHistoryRepository.findAll(specification);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Sold before August", result.get(0).getMoreInfo());
    }

    @Test
    void soldBefore_june_returnsNoSales() {
        Specification<Sale> specification = SalesReportSpecifications.soldBefore(LocalDateTime.parse("2021-06-30T00:00:00"));
        List<Sale> result = saleHistoryRepository.findAll(specification);
        Assertions.assertEquals(0, result.size());
    }

    @Test
    void soldAfter_june_returnsBothSales() {
        Specification<Sale> specification = SalesReportSpecifications.soldAfter(LocalDateTime.parse("2021-06-30T00:00:00"));
        List<Sale> result = saleHistoryRepository.findAll(specification);
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void soldAfter_august_returnsOneSale() {
        Specification<Sale> specification = SalesReportSpecifications.soldAfter(LocalDateTime.parse("2021-08-30T00:00:00"));
        List<Sale> result = saleHistoryRepository.findAll(specification);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Sold after August", result.get(0).getMoreInfo());
    }

    @Test
    void soldAfter_october_returnsNoSales() {
        Specification<Sale> specification = SalesReportSpecifications.soldAfter(LocalDateTime.parse("2021-10-30T00:00:00"));
        List<Sale> result = saleHistoryRepository.findAll(specification);
        Assertions.assertEquals(0, result.size());
    }
}
