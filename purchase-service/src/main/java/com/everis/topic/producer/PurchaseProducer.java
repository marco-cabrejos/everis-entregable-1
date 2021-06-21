package com.everis.topic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.Purchase;

@Component
public class PurchaseProducer {
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Value("${topic.name}")
	private String kafkaTopic;

	public void send(Purchase message) {
		
		kafkaTemplate.send(kafkaTopic, message);
		
	}
	
}
