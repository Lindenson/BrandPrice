package com.wolper.prices.adapters.in.web;

import com.wolper.prices.adapters.in.web.dto.PriceResponse;
import com.wolper.prices.adapters.in.web.mapper.PriceMapper;
import com.wolper.prices.port.in.GetPriceUseCase;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
@Validated
public class PriceController {

    private final GetPriceUseCase getPriceUseCase;

    @GetMapping("/final")
    public ResponseEntity<PriceResponse> getFinalPrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam @Min(0) Long productId,
            @RequestParam @Min(0) Long brandId
    ) {
        PriceResponse price = getPriceUseCase.getPrice(date, productId, brandId)
                .map(PriceMapper.INSTANCE::toPriceResponse)
                .orElse(null);

        return ResponseEntity.ok()
                .header("Cache-Control", "no-cache")
                .header("X-Request-ID", UUID.randomUUID().toString())
                .body(price);
    }
}

