package io.samjingwen.gamesales.batch;

import io.samjingwen.gamesales.entity.GameSales;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class GameSalesBatchConfig {

  @Bean
  public Job job(JobRepository jobRepository, Step insertDataMasterStep) {
    return new JobBuilder("gameSalesUploadJob", jobRepository).start(insertDataMasterStep).build();
  }

  @Bean
  public Step insertDataMasterStep(
      JobRepository jobRepository,
      Partitioner partitioner,
      Step insertDataStep,
      TaskExecutor gameSalesTaskExecutor) {
    return new StepBuilder("insertDataMasterStep", jobRepository)
        .partitioner("insertDataStep", partitioner)
        .step(insertDataStep)
        .taskExecutor(gameSalesTaskExecutor)
        .build();
  }

  @Bean
  public Step insertDataStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      FlatFileItemReader<GameSales> itemReader,
      ItemProcessor<GameSales, GameSales> itemProcessor,
      ItemWriter<GameSales> itemWriter) {
    return new StepBuilder("insertDataStep", jobRepository)
        .<GameSales, GameSales>chunk(1_000, transactionManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .build();
  }

  @Bean
  public ItemProcessor<GameSales, GameSales> gameSalesItemProcessor() {
    return new GameSalesItemValidator();
  }

  @StepScope
  @Bean
  public FlatFileItemReader<GameSales> gameSalesItemReader(
      @Value("#{stepExecutionContext['fileName']}") Resource resource) {
    return new FlatFileItemReaderBuilder<GameSales>()
        .name("gameSalesItemReader")
        .resource(resource)
        .delimited()
        .delimiter(",")
        .names("id", "gameNo", "gameName", "gameCode", "type", "costPrice", "dateOfSale", "tax", "salePrice")
        .fieldSetMapper(
            fieldSet -> {
              GameSales gameSales = new GameSales();
              gameSales.setId(fieldSet.readLong("id"));
              gameSales.setGameNo(fieldSet.readInt("gameNo"));
              gameSales.setGameName(fieldSet.readString("gameName"));
              gameSales.setGameCode(fieldSet.readString("gameCode"));
              gameSales.setType(fieldSet.readInt("type"));
              gameSales.setCostPrice(fieldSet.readBigDecimal("costPrice"));
              gameSales.setTax(fieldSet.readBigDecimal("tax"));
              gameSales.setSalePrice(fieldSet.readBigDecimal("salePrice"));

              String dateStr = fieldSet.readString("dateOfSale");
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
              gameSales.setDateOfSale(LocalDate.parse(dateStr, formatter));

              return gameSales;
            })
        .linesToSkip(1)
        .strict(true)
        .build();
  }

  @Bean
  public ItemWriter<GameSales> gameSalesItemWriter(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<GameSales>()
        .sql(
            "INSERT INTO game_sales (id, game_no, game_name, game_code, type, cost_price, tax, sale_price, date_of_sale) "
                + "VALUES (:id, :gameNo, :gameName, :gameCode, :type, :costPrice, :tax, :salePrice, :dateOfSale)")
        .dataSource(dataSource)
        .beanMapped()
        .build();
  }

  @Bean
  @StepScope
  public Partitioner partitioner(
      @Value("#{jobParameters['input.file.name']}") Resource[] resources) {
    MultiResourcePartitioner partitioner = new MultiResourcePartitioner();
    partitioner.setResources(resources);
    partitioner.partition(10);
    return partitioner;
  }

  @Bean
  public TaskExecutor gameSalesTaskExecutor() {
    return new SimpleAsyncTaskExecutor("gameSalesBatch-");
  }
}
