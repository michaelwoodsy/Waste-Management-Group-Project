package org.seng302.project.service_layer.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.seng302.project.repository_layer.model.LikedSaleListing;
import org.seng302.project.repository_layer.model.SaleListing;
import org.seng302.project.repository_layer.repository.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SaleListingService class.
 * These tests use mocking, which is why it is separate from the other SaleListingServiceTest class.
 */
@ExtendWith(MockitoExtension.class)
class SaleListingServiceWithMockingTest {

    @Mock
    private SaleListingRepository saleListingRepository;
    @Mock
    private LikedSaleListingRepository likedSaleListingRepository;
    private SaleListingService saleListingService;

    @BeforeEach
    void setup() {
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
}
