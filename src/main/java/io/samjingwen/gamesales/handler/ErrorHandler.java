package io.samjingwen.gamesales.handler;

import io.samjingwen.gamesales.error.UploadError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

  @ExceptionHandler(value = {UploadError.class})
  public ResponseEntity<String> handle() {
    return ResponseEntity.internalServerError().body("Error uploading file");
  }
}
