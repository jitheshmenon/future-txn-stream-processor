package com.abnamro.challenge.futuretxn.config;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

public interface StreamBinding {

  @Input(StreamConstants.EVENT_INPUT)
  KStream<String, String> inputStream();


  @Output(StreamConstants.EVENT_OUTPUT)
  KStream<String, Double> outputStream();
}
