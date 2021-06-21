package com.everis.topic.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.everis.model.CreditPayment;

@Component
public class CreditPaymentProducer {
	
	@Autowired
	private KafkaTemplate<String, CreditPayment> kafkaTemplate;

	private String creditPaymentTransactionTopic = "created-credit-payment-topic";

	public void sendCreditPaymentTransactionTopic(CreditPayment creditPayment) {
		
		kafkaTemplate.send(creditPaymentTransactionTopic, creditPayment);
		
	}
	
}
