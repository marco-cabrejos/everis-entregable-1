package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Account;
import com.everis.service.IAccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class Consumer {
	@Autowired
	private IAccountService accountService;
	
	@KafkaListener(topics = "created-account-topic", groupId = "withdrawal-group")
	public Mono<Void> retrieveCreatedAccount(String data) {
		Mono<String> monoData = Mono.just(data);
		
		return monoData
				.flatMap(json->{
					ObjectMapper objectMapper = new ObjectMapper();
					Account account = objectMapper.readValue(data, Account.class);					
				});
		
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Account account = objectMapper.readValue(data, Account.class);
			
			System.out.println("Se creó: "+account.getAccountNumber());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
