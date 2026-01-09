package com.wolper.prices.adapters.out.jdbc;

import com.wolper.prices.adapters.out.jdbc.mapper.PriceRowMapper;
import com.wolper.prices.core.domain.Price;
import com.wolper.prices.core.exceptions.InvalidPriceDataException;
import com.wolper.prices.port.out.PriceRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcPriceRepository implements PriceRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final PriceRowMapper rowMapper;

    private final String SQL = """
               SELECT BRAND_ID,
                      START_DATE,
                      END_DATE,
                      PRICE_LIST,
                      PRODUCT_ID,
                      PRIORITY,
                      PRICE,
                      CURR
               FROM PRICES
               WHERE BRAND_ID  = :brandId
                 AND PRODUCT_ID = :productId
                 AND START_DATE <= :date
                 AND END_DATE   >= :date
               ORDER BY PRIORITY DESC
               LIMIT 1;
            """;

    @Override
    public Optional<Price> findApplicablePrice(@NotNull LocalDateTime applicationDate,
                                               @NotNull Long productId, @NotNull Long brandId) {

        log.debug("fetching data for applicationDate {} and productId {}", applicationDate, productId);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("date", applicationDate)
                .addValue("productId", productId)
                .addValue("brandId", brandId);

        try {
            var prices = jdbcTemplate.query(SQL, params, rowMapper);
            return prices.isEmpty() ? Optional.empty() : Optional.of(prices.getFirst());

        } catch (DataAccessException ex) {
            log.error("Database error while fetching price for productId={}, brandId={}, date={}",
                    productId, brandId, applicationDate, ex);
            throw new InvalidPriceDataException("Database error", ex);
        }
    }
}
