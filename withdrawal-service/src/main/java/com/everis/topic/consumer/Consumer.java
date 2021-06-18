package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Account;
import com.everis.service.IAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class Consumer {
	@Autowired
	private IAccountService accountService;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@KafkaListener(topics = "created-account-topic", groupId = "withdrawal-group")
	public Disposable retrieveCreatedAccount(String data) throws Exception{
		System.out.println("procesando "+data);
		Account account = objectMapper.readValue(data, Account.class);
		return Mono.just(account)
				.log()
				.flatMap(accountService::update)
				.subscribe();
	}
}
