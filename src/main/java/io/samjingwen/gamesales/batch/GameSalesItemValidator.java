package io.samjingwen.gamesales.batch;

import io.samjingwen.gamesales.entity.GameSales;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;

public class GameSalesItemValidator implements ItemProcessor<GameSales, GameSales> {

  @Override
  public GameSales process(GameSales gameSales) {
    if (gameSales.getGameNo() < 1 || gameSales.getGameNo() > 100) {
      throw new IllegalArgumentException("Invalid gameNo: must be between 1 and 100.");
    }

    if (gameSales.getGameName().length() > 20) {
      throw new IllegalArgumentException("Invalid gameName: max length is 20 characters.");
    }

    if (gameSales.getGameCode().length() > 5) {
      throw new IllegalArgumentException("Invalid gameCode: max length is 5 characters.");
    }

    if (gameSales.getType() != 1 && gameSales.getType() != 2) {
      throw new IllegalArgumentException("Invalid type: must be 1 (Online) or 2 (Offline).");
    }

    if (gameSales.getCostPrice().compareTo(BigDecimal.valueOf(100)) > 0) {
      throw new IllegalArgumentException("Invalid costPrice: must not exceed 100.");
    }

    return gameSales;
  }
}
