package com.abnamro.challenge.futuretxn;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import java.util.Map;
import org.springframework.cloud.stream.schema.client.SchemaRegistryClient;

public class CustomKafkaAvroSerializer extends KafkaAvroDeserializer {

  public CustomKafkaAvroSerializer() {
    super();
    super.schemaRegistry = new MockSchemaRegistryClient();
  }

  public CustomKafkaAvroSerializer(SchemaRegistryClient client) {
    super(new MockSchemaRegistryClient());
  }

  public CustomKafkaAvroSerializer(SchemaRegistryClient client, Map<String, ?> props) {
    super(new MockSchemaRegistryClient(), props);
  }
}