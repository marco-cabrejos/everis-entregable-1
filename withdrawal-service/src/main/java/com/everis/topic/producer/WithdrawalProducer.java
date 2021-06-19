package com.everis.topic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.Withdrawal;

@Component
public class WithdrawalProducer {
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	private String withdrawalAccountTopic = "created-withdrawal-topic";

	public void sendWithdrawalAccountTopic(Withdrawal o) {
		kafkaTemplate.send(withdrawalAccountTopic, o);
	}
	
}
