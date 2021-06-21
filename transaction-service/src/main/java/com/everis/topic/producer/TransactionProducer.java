package com.everis.topic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.Transaction;

@Component
public class TransactionProducer {
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	private String withdrawalAccountTopic = "created-transaction-topic";

	public void sendCreatedTransactionAccountTopic(Transaction o) {
		
		kafkaTemplate.send(withdrawalAccountTopic, o);
		
	}
	
	public void sendCreatedTransactionPurchaseTopic(Transaction o) {
		
		kafkaTemplate.send(withdrawalAccountTopic, o);
		
	}
	
}
