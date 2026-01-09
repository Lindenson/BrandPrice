package com.wolper.prices.core.domain;

import lombok.NonNull;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
public class Price {

    @NonNull
    Long productId;

    @NonNull
    Long brandId;

    @NonNull
    Integer priceList;

    @NonNull
    LocalDateTime startDate;

    @NonNull
    LocalDateTime endDate;

    @NonNull
    BigDecimal price;

    @NonNull
    String currency;

    @NonNull
    Integer priority;
}
