package com.wolper.prices.adapters.out.jdbc.mapper;

import com.wolper.prices.core.domain.Price;
import com.wolper.prices.core.exceptions.InvalidPriceDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PriceRowMapperTest {

    private PriceRowMapper mapper;
    private ResultSet rs;

    @BeforeEach
    void setUp() {
        mapper = new PriceRowMapper();
        rs = mock(ResultSet.class);
    }

    @Test
    void testMapRowSuccessfully() throws SQLException {
        LocalDateTime start = LocalDateTime.of(2026, 1, 9, 10, 0);
        LocalDateTime end = start.plusDays(1);

        when(rs.getLong("BRAND_ID")).thenReturn(1L);
        when(rs.getLong("PRODUCT_ID")).thenReturn(100L);
        when(rs.getInt("PRICE_LIST")).thenReturn(2);
        when(rs.getTimestamp("START_DATE")).thenReturn(Timestamp.valueOf(start));
        when(rs.getTimestamp("END_DATE")).thenReturn(Timestamp.valueOf(end));
        when(rs.getBigDecimal("PRICE")).thenReturn(BigDecimal.valueOf(99.99));
        when(rs.getString("CURR")).thenReturn("EUR");
        when(rs.getInt("PRIORITY")).thenReturn(1);

        Price price = mapper.mapRow(rs, 0);

        assertEquals(1L, price.getBrandId());
        assertEquals(100L, price.getProductId());
        assertEquals(2, price.getPriceList());
        assertEquals(start, price.getStartDate());
        assertEquals(end, price.getEndDate());
        assertEquals(BigDecimal.valueOf(99.99), price.getPrice());
        assertEquals("EUR", price.getCurrency());
        assertEquals(1, price.getPriority());
    }

    @Test
    void testMapRowThrowsInvalidPriceDataExceptionOnNull() throws SQLException {
        when(rs.getLong("BRAND_ID")).thenThrow(new NullPointerException("brand id null"));

        InvalidPriceDataException ex = assertThrows(
                InvalidPriceDataException.class,
                () -> mapper.mapRow(rs, 0)
        );

        assertTrue(ex.getMessage().contains("Invalid data in row"));
        assertNotNull(ex.getCause());
        assertEquals(NullPointerException.class, ex.getCause().getClass());
    }
}
