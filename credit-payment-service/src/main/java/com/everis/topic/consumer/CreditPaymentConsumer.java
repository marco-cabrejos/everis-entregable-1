package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Purchase;
import com.everis.service.IPurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class CreditPaymentConsumer {
	
	@Autowired
	private IPurchaseService purchaseService;
		
	ObjectMapper objectMapper = new ObjectMapper();
	
	@KafkaListener(topics = "purchase-topic", groupId = "credit-payment-group")
	public Disposable retrieveCreatedPurchase(String data) throws Exception {
		
		Purchase account = objectMapper.readValue(data, Purchase.class);
		
		return Mono.just(account)
				.log()
				.flatMap(purchaseService::update)
				.subscribe();
		
	}
	
}
