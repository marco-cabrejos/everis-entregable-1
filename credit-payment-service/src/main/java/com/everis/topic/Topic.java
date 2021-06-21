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

import com.everis.model.CreditPayment;

@Configuration
public class Topic {
	
	/**
	 * Se crea topico para comunicación entre microservicios de pago de credito y transacciones
	 * 
	 * @return
	 */
	@Bean
	public NewTopic creditPaymentTransactionTopic() {
		
		return TopicBuilder
				.name("created-credit-payment-topic")
				.partitions(1)
				.replicas(1)
				.build();
		
	}
	
	@Bean
	public ProducerFactory<String, CreditPayment> producerFactory() {
		
		Map<String, Object> config = new HashMap<>();
		
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		return new DefaultKafkaProducerFactory<>(config);
		
	}
	
	@Bean	
	public KafkaTemplate<String, CreditPayment> kafkaTemplate() {
		
		return new KafkaTemplate<>(producerFactory());
		
	}
	
}
