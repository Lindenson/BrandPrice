package com.wolper.prices.adapters.in.web.mapper;

import com.wolper.prices.adapters.in.web.dto.PriceResponse;
import com.wolper.prices.core.domain.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceMapperTest {

    @Test
    void testToPriceResponse() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 12, 31, 23, 59, 59);

        Price price = Price.builder()
                .productId(35455L)
                .brandId(1L)
                .priceList(1)
                .startDate(start)
                .endDate(end)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .priority(0)
                .build();

        PriceResponse response = PriceMapper.INSTANCE.toPriceResponse(price);

        assertNotNull(response);
        assertEquals(price.getProductId(), response.productId());
        assertEquals(price.getBrandId(), response.brandId());
        assertEquals(price.getPriceList(), response.priceList());
        assertEquals(price.getStartDate(), response.startDate());
        assertEquals(price.getEndDate(), response.endDate());
        assertEquals(price.getPrice(), response.price());
        assertEquals(price.getCurrency(), response.curr());
    }

    @Test
    void testNullPriceResponse() {
        PriceResponse response = PriceMapper.INSTANCE.toPriceResponse(null);

        assertNull(response);
    }
}
