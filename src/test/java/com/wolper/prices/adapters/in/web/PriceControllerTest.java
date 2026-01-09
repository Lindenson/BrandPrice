package com.wolper.prices.adapters.in.web;

import com.wolper.prices.app.PriceServiceApplication;
import com.wolper.prices.core.domain.Price;
import com.wolper.prices.core.exceptions.InvalidPriceDataException;
import com.wolper.prices.port.in.GetPriceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
@ContextConfiguration(classes = PriceServiceApplication.class)
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetPriceUseCase getPriceUseCase;

    @Test
    void testGetFinalPriceReturnsPrice() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);

        Price price = getPriceForTest(date);

        when(getPriceUseCase.getPrice(date, 35455L, 1L))
                .thenReturn(Optional.of(price));

        mockMvc.perform(get("/prices/final")
                        .param("date", date.toString())
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.price").value(35.50));

        verify(getPriceUseCase, times(1)).getPrice(date, 35455L, 1L);
    }

    @Test
    void testGetFinalPriceReturns404WhenNoPrice() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);
        when(getPriceUseCase.getPrice(date, 35455L, 1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/prices/final")
                        .param("date", date.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isNotFound());

        verify(getPriceUseCase, times(1)).getPrice(date, 35455L, 1L);
    }

    @Test
    void testGetFinalPriceValidationError() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2026-01-09T10:00:00")
                        .param("productId", "35455"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFinalPriceTypeMismatch() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2026-01-09T10:00:00")
                        .param("productId", "abc")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFinalPriceConstraintViolation() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", "2026-01-09T10:00:00")
                        .param("productId", "-1")
                        .param("brandId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetFinalPriceThrowsInvalidPriceDataException() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);
        when(getPriceUseCase.getPrice(date, 35455L, 1L))
                .thenThrow(new InvalidPriceDataException("DB error"));

        mockMvc.perform(get("/prices/final")
                        .param("date", date.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testGetFinalPriceThrowsGenericException() throws Exception {
        LocalDateTime date = LocalDateTime.of(2026, 1, 9, 10, 0);
        when(getPriceUseCase.getPrice(date, 35455L, 1L))
                .thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(get("/prices/final")
                        .param("date", date.toString())
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isInternalServerError());
    }

    //helper
    private static Price getPriceForTest(LocalDateTime date) {
        return Price.builder()
                .brandId(1L)
                .productId(35455L)
                .priceList(1)
                .startDate(date.minusDays(1))
                .endDate(date.plusDays(1))
                .price(BigDecimal.valueOf(35.50))
                .currency("EUR")
                .priority(0)
                .build();
    }
}
