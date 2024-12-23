package io.samjingwen.gamesales.upload;

import io.samjingwen.gamesales.batch.GamesSalesBatchProperties;
import io.samjingwen.gamesales.error.UploadError;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadProcessor {

  private final int CHUNK_SIZE = 10_000;

  private final GamesSalesBatchProperties gamesSalesBatchProperties;
  private final JobLauncher jobLauncher;
  private final Job job;

  public void process(MultipartFile file) {
    String baseFilePath = saveFile(file);
    insertData(baseFilePath);
  }

  public String saveFile(MultipartFile file) {
    try {
      String baseFilePath = gamesSalesBatchProperties.getUploadDir() + "/" + UUID.randomUUID();
      Path uploadPath = Paths.get(baseFilePath);
      if (!Files.exists(uploadPath)) {
        Files.createDirectories(uploadPath);
      }

      String baseFileName = file.getOriginalFilename();

      try (BufferedReader reader =
          new BufferedReader(new InputStreamReader(file.getInputStream()))) {
        String header = reader.readLine();

        int fileCount = 0;
        int lineCount = 0;
        List<String> buffer = new ArrayList<>();
        buffer.add(header);

        String line;
        while ((line = reader.readLine()) != null) {
          buffer.add(line);
          lineCount++;

          if (lineCount % CHUNK_SIZE == 0) {
            fileCount++;
            String splitFileName = baseFileName.replace(".csv", "_" + fileCount + ".csv");
            Path splitFilePath = uploadPath.resolve(splitFileName);
            Files.write(splitFilePath, buffer);
            buffer.clear();
            buffer.add(header);
          }
        }

        if (buffer.size() > 1) {
          fileCount++;
          String splitFileName = baseFileName.replace(".csv", "_" + fileCount + ".csv");
          Path splitFilePath = uploadPath.resolve(splitFileName);
          Files.write(splitFilePath, buffer);
        }
      }
      return baseFilePath;
    } catch (IOException e) {
      log.error("Error uploading file", e);
      throw new UploadError();
    }
  }

  private void insertData(String file) {
    JobParameters jobParameters =
        new JobParametersBuilder()
            .addString("input.file.name", String.format("file://%s/*", file))
            .toJobParameters();

    try {
      jobLauncher.run(job, jobParameters);
    } catch (JobExecutionAlreadyRunningException
        | JobRestartException
        | JobInstanceAlreadyCompleteException
        | JobParametersInvalidException e) {
      log.error("Error saving data", e);
      throw new UploadError();
    }
  }
}
