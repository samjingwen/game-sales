package io.samjingwen.gamesales.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class GameSales {
  private Long id;
  private Integer gameNo;
  private String gameName;
  private String gameCode;
  private Integer type;
  private BigDecimal costPrice;
  private BigDecimal tax;
  private BigDecimal salePrice;
  private LocalDate dateOfSale;
}
