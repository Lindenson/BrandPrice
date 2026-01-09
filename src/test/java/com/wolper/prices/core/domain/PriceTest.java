package com.wolper.prices.core.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class PriceTest {

    @Test
    void testBuilderCreatesPriceCorrectly() {
        LocalDateTime start = LocalDateTime.of(2026, 1, 9, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 1, 9, 12, 0);

        Price price = getPriceForTest(start, end);

        assertEquals(35455L, price.getProductId());
        assertEquals(1L, price.getBrandId());
        assertEquals(1, price.getPriceList());
        assertEquals(start, price.getStartDate());
        assertEquals(end, price.getEndDate());
        assertEquals(new BigDecimal("35.50"), price.getPrice());
        assertEquals("EUR", price.getCurrency());
        assertEquals(0, price.getPriority());
    }


    @ParameterizedTest(name = "Null check for field: {0}")
    @MethodSource("nullFieldCases")
    void shouldThrowNpeWhenSettingNullToRequiredField(
            String fieldName,
            Consumer<Price.PriceBuilder> nullifier) {

        Price.PriceBuilder builder = Price.builder();

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> nullifier.accept(builder)
        );

        assertTrue(
                ex.getMessage().contains(fieldName),
                "Exception message should contain field name"
        );
    }

    //helpers
    static Stream<Arguments> nullFieldCases() {
        return Stream.of(
                Arguments.of("productId", (Consumer<Price.PriceBuilder>) b -> b.productId(null)),
                Arguments.of("brandId", (Consumer<Price.PriceBuilder>) b -> b.brandId(null)),
                Arguments.of("priceList", (Consumer<Price.PriceBuilder>) b -> b.priceList(null)),
                Arguments.of("startDate", (Consumer<Price.PriceBuilder>) b -> b.startDate(null)),
                Arguments.of("endDate", (Consumer<Price.PriceBuilder>) b -> b.endDate(null)),
                Arguments.of("price", (Consumer<Price.PriceBuilder>) b -> b.price(null)),
                Arguments.of("currency", (Consumer<Price.PriceBuilder>) b -> b.currency(null)),
                Arguments.of("priority", (Consumer<Price.PriceBuilder>) b -> b.priority(null))
        );
    }


    private static Price getPriceForTest(LocalDateTime start, LocalDateTime end) {
        return Price.builder()
                .productId(35455L)
                .brandId(1L)
                .priceList(1)
                .startDate(start)
                .endDate(end)
                .price(new BigDecimal("35.50"))
                .currency("EUR")
                .priority(0)
                .build();
    }
}
