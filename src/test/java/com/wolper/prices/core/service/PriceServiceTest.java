package com.wolper.prices.core.service;

import com.wolper.prices.core.domain.Price;
import com.wolper.prices.core.exceptions.InvalidPriceDataException;
import com.wolper.prices.port.out.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceServiceTest {

    private PriceRepository repository;
    private PriceService service;

    private final LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);

    @BeforeEach
    void setUp() {
        repository = mock(PriceRepository.class);
        service = new PriceService(repository);
    }

    @Test
    void testGetPriceReturnsPriceIfFound() {
        Price price = getPriceForTest();

        when(repository.findApplicablePrice(date, 1L, 1L))
                .thenReturn(Optional.of(price));

        Optional<Price> result = service.getPrice(date, 1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().getPriceList());
        assertEquals(1, result.get().getPriority());
        verify(repository, times(1)).findApplicablePrice(date, 1L, 1L);
    }

    @Test
    void testGetPriceReturnsEmptyIfNotFound() {
        when(repository.findApplicablePrice(date, 1L, 1L))
                .thenReturn(Optional.empty());

        Optional<Price> result = service.getPrice(date, 1L, 1L);

        assertTrue(result.isEmpty());
        verify(repository, times(1)).findApplicablePrice(date, 1L, 1L);
    }

    @Test
    void testGetPricePropagatesException() {
        when(repository.findApplicablePrice(date, 1L, 1L))
                .thenThrow(new InvalidPriceDataException("DB error"));

        assertThrows(InvalidPriceDataException.class,
                () -> service.getPrice(date, 1L, 1L));

        verify(repository, times(1)).findApplicablePrice(date, 1L, 1L);
    }

    // helper
    private Price getPriceForTest() {
        return Price.builder()
                .productId(1L)
                .brandId(1L)
                .priceList(2)
                .startDate(date.minusDays(1))
                .endDate(date.plusDays(1))
                .price(BigDecimal.valueOf(12))
                .currency("EUR")
                .priority(1)
                .build();
    }
}
