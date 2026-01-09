package com.wolper.prices.core.service;

import com.wolper.prices.core.domain.Price;
import com.wolper.prices.port.in.GetPriceUseCase;
import com.wolper.prices.port.out.PriceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PriceService implements GetPriceUseCase {

    private final PriceRepository repository;

    @Override
    public Optional<Price> getPrice(@NotNull LocalDateTime date, @NotNull Long productId, @NotNull Long brandId) {
        log.debug("getPrice called with date={}, productId={}, brandId={}", date, productId, brandId);

        Optional<Price> applicablePrice = repository.findApplicablePrice(date, productId, brandId);

        applicablePrice.ifPresentOrElse(price ->
                        log.info("Price applied: productId={}, brandId={}, priceList={}, finalPrice={}, from={} to={}",
                                price.getProductId(),
                                price.getBrandId(),
                                price.getPriceList(),
                                price.getPrice(),
                                price.getStartDate(),
                                price.getEndDate()),
                () -> log.info("No applicable price found for productId={}, brandId={}, date={}",
                        productId, brandId, date)
        );

        log.debug("getPrice result: {}", applicablePrice.orElse(null));

        return applicablePrice;
    }
}
