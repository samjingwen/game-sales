package io.samjingwen.gamesales.query;

import io.samjingwen.gamesales.entity.GameSales;
import io.samjingwen.gamesales.entity.GameSalesMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueryProcessor {

  private static final int PAGE_SIZE = 100;

  private final JdbcTemplate jdbcTemplate;

  public List<GameSales> queryGameSales(
      String fromDate, String toDate, Double salePrice, String priceFilter, Long cursor) {
    String baseQuery = "SELECT * FROM game_sales WHERE 1=1";

    List<Object> params = new ArrayList<>();

    if (fromDate != null && toDate != null) {
      baseQuery += " AND date_of_sale BETWEEN ? AND ?";
      params.add(LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      params.add(LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    if (salePrice != null && priceFilter != null) {
      if (priceFilter.equalsIgnoreCase("less")) {
        baseQuery += " AND sale_price < ?";
      } else if (priceFilter.equalsIgnoreCase("more")) {
        baseQuery += " AND sale_price > ?";
      }
      params.add(salePrice);
    }

    if (cursor != null) {
      baseQuery += " AND id > ?";
      params.add(cursor);
    }

    baseQuery += " ORDER BY id ASC LIMIT ?";
    params.add(PAGE_SIZE);

    return jdbcTemplate.query(baseQuery, new GameSalesMapper(), params.toArray());
  }
}
