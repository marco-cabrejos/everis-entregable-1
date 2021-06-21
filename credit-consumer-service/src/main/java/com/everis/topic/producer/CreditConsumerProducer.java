package com.everis.topic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.CreditConsumer;

@Component
public class CreditConsumerProducer {
	
	@Autowired
	private KafkaTemplate<String, CreditConsumer> kafkaTemplate;

	private String creditConsumerTransactionTopic = "created-credit-consumer-topic";

	public void sendCreditConsumerTransactionTopic(CreditConsumer creditConsumer) {
		
		kafkaTemplate.send(creditConsumerTransactionTopic, creditConsumer);
		
	}
	
}
