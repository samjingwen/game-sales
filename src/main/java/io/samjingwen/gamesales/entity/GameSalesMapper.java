package io.samjingwen.gamesales.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

public class GameSalesMapper implements RowMapper<GameSales> {
  @Override
  public GameSales mapRow(ResultSet rs, int rowNum) throws SQLException {
    GameSales gameSales = new GameSales();
    gameSales.setId(rs.getLong("id"));
    gameSales.setGameNo(rs.getInt("game_no"));
    gameSales.setGameName(rs.getString("game_name"));
    gameSales.setGameCode(rs.getString("game_code"));
    gameSales.setType(rs.getInt("type"));
    gameSales.setCostPrice(rs.getBigDecimal("cost_price"));
    gameSales.setTax(rs.getBigDecimal("tax"));
    gameSales.setSalePrice(rs.getBigDecimal("sale_price"));
    gameSales.setDateOfSale(
        rs.getTimestamp("date_of_sale").toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    return gameSales;
  }
}
