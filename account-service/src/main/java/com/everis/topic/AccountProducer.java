package com.everis.topic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.Account;

@Component
public class AccountProducer {
	
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;

	private String createdAccountTopic = "created-account-topic";

	public void sendCreatedAccount(Account o) {
		kafkaTemplate.send(createdAccountTopic, o);
	}

}