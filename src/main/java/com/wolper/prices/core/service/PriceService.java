package com.wolper.prices.core.service;

import com.wolper.prices.core.domain.Price;
import com.wolper.prices.port.in.GetPriceUseCase;
import com.wolper.prices.port.out.PriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriceService implements GetPriceUseCase {

    private final PriceRepository repository;

    @Override
    public Optional<Price> getPrice(LocalDateTime date, Long productId, Long brandId) {
        return repository.findApplicablePrice(date, productId, brandId);
    }
}
