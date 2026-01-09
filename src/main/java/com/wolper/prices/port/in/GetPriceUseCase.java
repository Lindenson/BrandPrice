package com.wolper.prices.port.in;

import com.wolper.prices.core.domain.Price;

import java.time.LocalDateTime;
import java.util.Optional;

public interface GetPriceUseCase {
    Optional<Price> getPrice(LocalDateTime date, Long productId, Long brandId);
}
