package io.samjingwen.gamesales.batch;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GamesSalesBatchProperties {

  @Value("${io.samjingwen.game-sales.upload-dir}")
  private String uploadDir;
}
