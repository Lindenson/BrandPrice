package com.wolper.prices.adapters.out.jdbc;

import com.wolper.prices.adapters.out.jdbc.mapper.PriceRowMapper;
import com.wolper.prices.core.domain.Price;
import com.wolper.prices.core.exceptions.InvalidPriceDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JdbcPriceRepositoryTest {

    private NamedParameterJdbcTemplate jdbcTemplate;
    private PriceRowMapper rowMapper;
    private JdbcPriceRepository repository;

    private final LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(NamedParameterJdbcTemplate.class);
        rowMapper = mock(PriceRowMapper.class);
        repository = new JdbcPriceRepository(jdbcTemplate, rowMapper);
    }

    @Test
    void testFindApplicablePriceReturnsPrice() {
        Price price = getPriceForTest();

        when(jdbcTemplate.query(anyString(), (SqlParameterSource) any(), eq(rowMapper))).thenReturn(List.of(price));

        Optional<Price> result = repository.findApplicablePrice(date, 100L, 1L);

        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getProductId());
        verify(jdbcTemplate, times(1)).query(anyString(), (SqlParameterSource) any(), eq(rowMapper));
    }

    @Test
    void testFindApplicablePriceReturnsEmptyIfNull() {
        when(jdbcTemplate.query(anyString(), (SqlParameterSource) any(), eq(rowMapper))).thenReturn(List.of());

        Optional<Price> result = repository.findApplicablePrice(date, 100L, 1L);

        assertTrue(result.isEmpty());
        verify(jdbcTemplate, times(1)).query(anyString(), (SqlParameterSource) any(), eq(rowMapper));
    }

    @Test
    void testFindApplicablePriceThrowsInvalidPriceDataExceptionOnDbError() {
        when(jdbcTemplate.query(anyString(), (SqlParameterSource) any(), eq(rowMapper)))
                .thenThrow(new DataAccessException("DB error") {
                });

        InvalidPriceDataException ex = assertThrows(
                InvalidPriceDataException.class,
                () -> repository.findApplicablePrice(date, 100L, 1L)
        );

        assertTrue(ex.getMessage().contains("Database error"));
        verify(jdbcTemplate, times(1)).query(anyString(), (SqlParameterSource) any(), eq(rowMapper));
    }

    // helper
    private Price getPriceForTest() {
        return Price.builder()
                .productId(100L)
                .brandId(1L)
                .priceList(1)
                .startDate(date.minusDays(1))
                .endDate(date.plusDays(1))
                .price(BigDecimal.valueOf(99.99))
                .currency("EUR")
                .priority(1)
                .build();
    }
}
