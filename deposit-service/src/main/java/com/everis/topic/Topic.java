package com.everis.topic;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.everis.model.Deposit;

@Configuration
public class Topic {
	
	/**
	 * Se crea topico para comunicación entre microservicios de depositos y transacciones
	 * 
	 * @return
	 */
	@Bean
	public NewTopic depositAccountTopic() {
		
		return TopicBuilder
				.name("created-deposit-topic")
				.partitions(1)
				.replicas(1)
				.build();
		
	}
	
	@Bean
	public ProducerFactory<String, Deposit> producerFactory() {
		
		Map<String, Object> config = new HashMap<>();
		
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		return new DefaultKafkaProducerFactory<>(config);
		
	}
	
	@Bean	
	public KafkaTemplate<String, Deposit> kafkaTemplate() {
		
		return new KafkaTemplate<>(producerFactory());
		
	}
	
}
