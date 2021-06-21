package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Account;
import com.everis.model.Purchase;
import com.everis.service.IAccountService;
import com.everis.service.IPurchaseService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class WithdrawalConsumer {
	
	@Autowired
	private IAccountService accountService;

	@Autowired
	private IPurchaseService purchaseService;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@KafkaListener(topics = "created-account-topic", groupId = "withdrawal-group")
	public Disposable retrieveCreatedAccount(String data) throws Exception {
		
		Account account = objectMapper.readValue(data, Account.class);
		
		return Mono.just(account)
				.log()
				.flatMap(accountService::update)
				.subscribe();
		
	}
	
	@KafkaListener(topics = "purchase-topic", groupId = "withdrawal-group")
	public Disposable retrieveCreatedPurchase(String data) throws Exception {
		
		Purchase purchase = objectMapper.readValue(data, Purchase.class);
		
		return Mono.just(purchase)
				.log()
				.flatMap(purchaseService::update)
				.subscribe();
		
	}
	
}
