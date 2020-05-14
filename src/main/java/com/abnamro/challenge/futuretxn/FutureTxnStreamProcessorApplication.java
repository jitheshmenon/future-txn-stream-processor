package com.abnamro.challenge.futuretxn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.schema.client.EnableSchemaRegistryClient;

@SpringBootApplication
@EnableSchemaRegistryClient
public class FutureTxnStreamProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(FutureTxnStreamProcessorApplication.class, args);
	}

}
