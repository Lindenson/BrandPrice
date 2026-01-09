package com.wolper.prices.integration;

import com.wolper.prices.app.PriceServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = PriceServiceApplication.class)
@ActiveProfiles("test")
class PriceApplicationIT {

    @Autowired
    private MockMvc mockMvc;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void test1_14_06_10_00() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", LocalDateTime.of(2020, 6, 14, 10, 0).format(formatter))
                        .param("productId", "35455")
                        .param("brandId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));
    }

    @Test
    void test2_14_06_16_00() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", LocalDateTime.of(2020, 6, 14, 16, 0).format(formatter))
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.price").value(25.45));
    }

    @Test
    void test3_14_06_21_00() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", LocalDateTime.of(2020, 6, 14, 21, 0).format(formatter))
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));
    }

    @Test
    void test4_15_06_10_00() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", LocalDateTime.of(2020, 6, 15, 10, 0).format(formatter))
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.price").value(30.50));
    }

    @Test
    void test5_16_06_21_00() throws Exception {
        mockMvc.perform(get("/prices/final")
                        .param("date", LocalDateTime.of(2020, 6, 16, 21, 0).format(formatter))
                        .param("productId", "35455")
                        .param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.price").value(38.95));
    }
}
