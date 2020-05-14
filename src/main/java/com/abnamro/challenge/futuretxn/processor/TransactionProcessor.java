package com.abnamro.challenge.futuretxn.processor;

import com.abnamro.challenge.futuretxn.config.StreamBinding;
import com.abnamro.challenge.futuretxn.config.StreamConstants;
import com.abnamro.challenge.futuretxn.mapper.InputValueMapper;
import com.abnamro.challenge.futuretxn.model.ClientInformation;
import com.abnamro.challenge.futuretxn.model.InputRecord;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;

@Slf4j
@EnableBinding(StreamBinding.class)
public class TransactionProcessor {

  @Autowired
  private InputValueMapper inputValueMapper;

  @StreamListener
  @SendTo(StreamConstants.EVENT_OUTPUT)
  public KStream<String, Double> process(@Input(StreamConstants.EVENT_INPUT)  final KStream<String, String> eventStream) {

    eventStream.mapValues(inputValueMapper)
            .map((key, value) -> new KeyValue<>(value.getClientInformation(), value))
        .groupByKey(Grouped.with(new JsonSerde<>(ClientInformation.class), new JsonSerde<>(InputRecord.class)))
        .windowedBy(TimeWindows.of(Duration.ofDays(1)))
        .count(Materialized.as("client-counts"))
        .toStream().to("CLIENT_COUNT");

    eventStream
        .mapValues(inputValueMapper)
        .map((key, value) -> new KeyValue<>(getCustomerProductStr(value), value.getTransactionAmount()))
        .groupByKey(Grouped.with(Serdes.String(), Serdes.Double()))
        .reduce(Double::sum, Materialized.as("customer_product_total"))
        .toStream()
        .peek((key,value) -> log.info("Grouped Key -> {} Value -> {}", key, value))
        .to("CUSTOMER_PRODUCT_TOTAL", Produced.with(Serdes.String(), Serdes.Double()));

    return eventStream
        .mapValues(inputValueMapper)
        .map((key, value) -> new KeyValue<>(value.getExternalNumber(), value.getTransactionAmount()));
  }

  private String getCustomerProductStr(InputRecord value) {
    return value.getClientInformation().getClientNumber() + "_" + value.getProductInformation().getProductGroupCode();
  }
}
