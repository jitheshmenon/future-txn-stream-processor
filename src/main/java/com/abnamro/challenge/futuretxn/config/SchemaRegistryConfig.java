package com.abnamro.challenge.futuretxn.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.schema.client.ConfluentSchemaRegistryClient;
import org.springframework.cloud.stream.schema.client.SchemaRegistryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SchemaRegistryConfig {

  @Bean
  public SchemaRegistryClient schemaRegistryClient(final @Value("${spring.cloud.stream.schemaRegistryClient.endpoint}") String endpoint){
    log.info("Schema Registry endpoint : {}", endpoint);
    ConfluentSchemaRegistryClient client = new ConfluentSchemaRegistryClient();
    client.setEndpoint(endpoint);
    return client;
  }
}
