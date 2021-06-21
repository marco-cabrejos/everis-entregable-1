package com.everis.topic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.Customer;

@Component
public class CustomerProducer {
	@Autowired
	private KafkaTemplate<String, Object> kafkaTemplate;
	private String createdCustomerTopic = "saved-customer-topic";

	public void sendSavedCustomerTopic(Customer data) {
		kafkaTemplate.send(createdCustomerTopic, data);
	}
}
