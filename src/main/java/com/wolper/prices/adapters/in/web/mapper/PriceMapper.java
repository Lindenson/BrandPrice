package com.wolper.prices.adapters.in.web.mapper;

import com.wolper.prices.adapters.in.web.dto.PriceResponse;
import com.wolper.prices.core.domain.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PriceMapper {

    PriceMapper INSTANCE = Mappers.getMapper(PriceMapper.class);

    @Mapping(target = "curr", source = "currency")
    PriceResponse toPriceResponse(Price price);
}
