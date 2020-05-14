package com.abnamro.challenge.futuretxn.scheduler;


import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ScheduledTask {

  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  @Autowired
  private InteractiveQueryService interactiveQueryService;

  @Scheduled(cron = "0 0 * * * *")
  public void scheduleTaskWithCronExpression() {
    log.info("Report Extract Task :: Execution Start Time - {}", dateTimeFormatter.format(LocalDateTime.now()));

    final ReadOnlyKeyValueStore<String, Double> prodTotal =
        interactiveQueryService.getQueryableStore("customer_product_total", QueryableStoreTypes.keyValueStore());

    try (CSVPrinter writer = new CSVPrinter(new FileWriter("Output.csv"), CSVFormat.DEFAULT)) {
      KeyValueIterator<String, Double> result = prodTotal.all();
      if (result.hasNext()) {
        while (result.hasNext()) {
          KeyValue<String, Double> kvEntry = result.next();
          writer.printRecord(kvEntry.key, Double.toString(kvEntry.value));
        }
      }
    } catch (IOException ioe) {
      log.error("Unable to process the scheduled Job", ioe);
    }

    log.info("Report Extract Task :: Execution End Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
  }

}