package com.abnamro.challenge.futuretxn;

import static org.assertj.core.api.Assertions.assertThat;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import java.util.Collections;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.DoubleDeserializer;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmbeddedKafkaApplicationTests {

	private static final String INPUT_TOPIC = "connect-input";
	private static final String OUTPUT_TOPIC = "output-report";
	private static final String GROUP_NAME = "embeddedKafkaApplication";
	private static final String SAMPLE_REQUEST = "315CL  432100020001SGXDC FUSGX NK    20100910JPY01B 0000000001 0000000000000000000060DUSD000000000030DUSD000000000000DJPY201008200012380     688032000092500000000             O";

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, OUTPUT_TOPIC);

	@BeforeClass
	public static void setup() {
		System.setProperty("spring.cloud.stream.kafka.binder.brokers", embeddedKafka.getEmbeddedKafka().getBrokersAsString());
	}

	@Bean
	private SchemaRegistryClient schemaRegistryClient() {
		return new MockSchemaRegistryClient();
	}

	@Test
	public void testSendReceive() {

		Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
		senderProps.put("key.serializer", ByteArraySerializer.class);
		senderProps.put("value.serializer", ByteArraySerializer.class);
		senderProps.put("schema.registry.url", "http://localhost:8081");
		DefaultKafkaProducerFactory<byte[], byte[]> pf = new DefaultKafkaProducerFactory<>(senderProps);
		KafkaTemplate<byte[], byte[]> template = new KafkaTemplate<>(pf, true);
		template.setDefaultTopic(INPUT_TOPIC);
		template.sendDefault(SAMPLE_REQUEST.getBytes());

		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka.getEmbeddedKafka());
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerProps.put("key.deserializer", ByteArrayDeserializer.class);
		consumerProps.put("value.deserializer", DoubleDeserializer.class);

		//DefaultKafkaConsumerFactory<byte[], Serde<InputRecord>> cf = new DefaultKafkaConsumerFactory<>(consumerProps);

		Consumer<byte[], Double> consumer =  new KafkaConsumer<>(consumerProps);
		consumer.subscribe(Collections.singleton(OUTPUT_TOPIC));
		ConsumerRecords<byte[], Double> records = consumer.poll(10_000);
		consumer.commitSync();

		assertThat(records.count()).isEqualTo(1);
		assertThat(records.iterator().next().value()).isEqualTo(1);
	}

}
