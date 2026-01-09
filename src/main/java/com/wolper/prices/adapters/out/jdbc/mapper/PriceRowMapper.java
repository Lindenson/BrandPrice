package com.wolper.prices.adapters.out.jdbc.mapper;

import com.wolper.prices.core.domain.Price;
import com.wolper.prices.core.exceptions.InvalidPriceDataException;
import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class PriceRowMapper implements RowMapper<Price> {

    @Override
    public Price mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
        try {
            return Price.builder()
                    .brandId(rs.getLong("BRAND_ID"))
                    .productId(rs.getLong("PRODUCT_ID"))
                    .priceList(rs.getInt("PRICE_LIST"))
                    .startDate(rs.getTimestamp("START_DATE").toLocalDateTime())
                    .endDate(rs.getTimestamp("END_DATE").toLocalDateTime())
                    .price(rs.getBigDecimal("PRICE"))
                    .currency(rs.getString("CURR"))
                    .priority(rs.getInt("PRIORITY"))
                    .build();
        } catch (NullPointerException e) {
            throw new InvalidPriceDataException(
                    "Invalid data in row " + rowNum, e
            );
        }
    }
}
