package com.wolper.prices.adapters.in.web;

import com.wolper.prices.adapters.in.web.dto.PriceRequest;
import com.wolper.prices.core.domain.Price;
import com.wolper.prices.port.in.GetPriceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    private final GetPriceUseCase getPriceUseCase;

    @GetMapping("/final")
    public ResponseEntity<Price> getFinalPrice(@Valid PriceRequest request) {

        log.info("final called from price controller with request {} ", request);
        return getPriceUseCase.getPrice(
                        request.getDate(),
                        request.getProductId(),
                        request.getBrandId()
                )
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

