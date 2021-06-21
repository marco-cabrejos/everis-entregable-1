package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Account;
import com.everis.model.Transaction;
import com.everis.service.IAccountService;
import com.everis.topic.producer.AccountProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class Consumer {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private AccountProducer producer;

	@KafkaListener(topics = "created-transaction-topic", groupId = "account-group")
	public Disposable retrieveCreatedTransaction(String data) throws Exception {
		
		Transaction transaction = objectMapper.readValue(data, Transaction.class);
		
		Mono<Account> monoAccount = accountService.findById(transaction.getAccount().getId());
		
		Mono<Transaction> monoTransaction = Mono.just(transaction);
		
		return monoAccount
				.zipWith(monoTransaction, (a,b) -> {
					if(transaction.getTransactionType().equals("RETIRO")) {
						a.setCurrentBalance(a.getCurrentBalance() - transaction.getTransactionAmount());
					} else if (transaction.getTransactionType().equals("DEPOSITO")) {
						a.setCurrentBalance(a.getCurrentBalance() + transaction.getTransactionAmount());						
					}
					producer.sendCreatedAccount(a);
					return a;
				})
				.flatMap(accountService::update)
				.subscribe();
		
	}
	
}
