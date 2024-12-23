package io.samjingwen.gamesales.upload;

import io.samjingwen.gamesales.entity.GameSales;
import io.samjingwen.gamesales.query.QueryProcessor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UploadController {

  private final UploadProcessor uploadProcessor;
  private final QueryProcessor queryProcessor;

  @PostMapping("/import")
  public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("File is empty");
    }

    uploadProcessor.process(file);
    return ResponseEntity.ok("success");
  }

  @GetMapping("/getGameSales")
  public List<GameSales> getGameSales(
      @RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String toDate,
      @RequestParam(required = false) Double salePrice,
      @RequestParam(required = false) String priceFilter,
      @RequestParam(required = false) Long cursor) {
    return queryProcessor.queryGameSales(fromDate, toDate, salePrice, priceFilter, cursor);
  }
}
