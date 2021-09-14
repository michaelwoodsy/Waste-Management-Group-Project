package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.seng302.project.AbstractInitializer;
import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.repository.*;
import org.seng302.project.service_layer.dto.sale_listings.GetSaleListingDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SaleListingService class.
 * These tests use mocking, which is why it is separate from the other SaleListingServiceTest class.
 */
@ExtendWith(MockitoExtension.class)
class SaleListingServiceWithMockingTest extends AbstractInitializer {

    @Mock
    private SaleListingRepository saleListingRepository;
    @Mock
    private LikedSaleListingRepository likedSaleListingRepository;
    private SaleListingService saleListingService;

    List<SaleListing> listings;

    @BeforeEach
    void setup() {
        this.initialise();
        listings = this.getSaleListings();
        saleListingService = new SaleListingService(
                Mockito.mock(UserService.class),
                Mockito.mock(BusinessService.class),
                saleListingRepository,
                likedSaleListingRepository,
                Mockito.mock(SaleHistoryRepository.class),
                Mockito.mock(InventoryItemRepository.class),
                Mockito.mock(UserRepository.class),
                Mockito.mock(UserNotificationRepository.class)
        );
    }

    /**
     * Tests the deleteExpiredSaleListings method doesn't call the delete method on likedSaleListingRepository
     * when there are no likedSaleListings on expired listings
     */
    @Test
    void deleteExpiredSaleListings_noLikes_doesntCallLikeRepo() {
        saleListingService.deleteExpiredSaleListings();
        verify(likedSaleListingRepository, times(0)).deleteAll(any(List.class));
    }

    /**
     * Tests the deleteExpiredSaleListings method calls the delete method with returned likedSaleListings
     * returned from the likedSaleListingRepository
     */
    @Test
    void deleteExpiredSaleListings_withLikes_callsLikeRepo() {
        var returnedExpiredLikes = List.of(new LikedSaleListing());
        when(likedSaleListingRepository.findAll(any(Specification.class))).thenReturn(returnedExpiredLikes);
        saleListingService.deleteExpiredSaleListings();
        ArgumentCaptor<List<LikedSaleListing>> likedListCaptor = ArgumentCaptor.forClass(List.class);
        verify(likedSaleListingRepository, times(1)).deleteAll(likedListCaptor.capture());
        Assertions.assertEquals(returnedExpiredLikes, likedListCaptor.getValue());
    }

    /**
     * Tests the deleteExpiredSaleListings method calls the delete method with returned SaleListings that are expired
     */
    @Test
    void deleteExpiredSaleListings_listingsExpired_callsDeleteListings() {
        var returnedExpiredListings = List.of(new SaleListing());
        when(saleListingRepository.findAllByClosesBefore(any(LocalDateTime.class))).thenReturn(returnedExpiredListings);
        saleListingService.deleteExpiredSaleListings();
        ArgumentCaptor<List<SaleListing>> listCaptor = ArgumentCaptor.forClass(List.class);
        verify(saleListingRepository, times(1)).deleteAll(listCaptor.capture());
        Assertions.assertEquals(returnedExpiredListings, listCaptor.getValue());
    }

    /**
     * Tests the successful case for getting popular sale listings from country new zealand
     */
    @Test
    void popularListings_fromNewZealand_success(){
        List<List<Object>> response = Arrays.asList(
                Arrays.asList(listings.get(2), Long.valueOf("10")),
                Arrays.asList(listings.get(3), Long.valueOf("6"))
        );

        doReturn(response).when(likedSaleListingRepository).findPopularByCountry(any(String.class), any(Pageable.class));
        List<GetSaleListingDTO> listings = saleListingService.getPopularListings("New Zealand");
        Assertions.assertEquals(2, listings.size());

        Assertions.assertEquals("New Zealand", listings.get(0).getBusiness().getAddress().getCountry());
        Assertions.assertEquals("New Zealand", listings.get(1).getBusiness().getAddress().getCountry());

        Assertions.assertTrue(listings.get(0).getLikes() >= listings.get(1).getLikes());
    }

    /**
     * Tests the successful case for getting popular sale listings from country the netherlands
     */
    @Test
    void popularListings_fromNetherlands_success(){
        List<List<Object>> response = Arrays.asList(
                Arrays.asList(listings.get(0), Long.valueOf("20")),
                Arrays.asList(listings.get(1), Long.valueOf("4"))
        );

        doReturn(response).when(likedSaleListingRepository).findPopularByCountry(any(String.class), any(Pageable.class));
        List<GetSaleListingDTO> listings = saleListingService.getPopularListings("Netherlands");
        Assertions.assertEquals(2, listings.size());

        Assertions.assertEquals("Netherlands", listings.get(0).getBusiness().getAddress().getCountry());
        Assertions.assertEquals("Netherlands", listings.get(1).getBusiness().getAddress().getCountry());

        Assertions.assertTrue(listings.get(0).getLikes() >= listings.get(1).getLikes());
    }

    /**
     * Tests the successful case for getting popular sale listings worldwide
     */
    @Test
    void popularListings_worldwide_success(){
        List<List<Object>> response = Arrays.asList(
                Arrays.asList(listings.get(0), Long.valueOf("20")),
                Arrays.asList(listings.get(1), Long.valueOf("10")),
                Arrays.asList(listings.get(2), Long.valueOf("4")),
                Arrays.asList(listings.get(3), Long.valueOf("1"))
        );

        doReturn(response).when(likedSaleListingRepository).findPopular(any(Pageable.class));
        List<GetSaleListingDTO> listings = saleListingService.getPopularListings(null);
        Assertions.assertEquals(4, listings.size());

        Assertions.assertTrue(listings.get(0).getLikes() >= listings.get(1).getLikes());
        Assertions.assertTrue(listings.get(1).getLikes() >= listings.get(2).getLikes());
        Assertions.assertTrue(listings.get(2).getLikes() >= listings.get(3).getLikes());
    }
}
