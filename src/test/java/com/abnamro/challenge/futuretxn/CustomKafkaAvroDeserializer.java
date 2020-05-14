package com.abnamro.challenge.futuretxn;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;

public class CustomKafkaAvroDeserializer extends KafkaAvroDeserializer {
  @Override
  public Object deserialize(String topic, byte[] bytes) {
    return super.deserialize(topic, bytes);
  }
}