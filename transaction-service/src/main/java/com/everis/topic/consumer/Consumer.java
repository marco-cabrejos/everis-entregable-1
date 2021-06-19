package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Transaction;
import com.everis.model.Withdrawal;
import com.everis.service.ITransactionService;
import com.everis.topic.producer.TransactionProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class Consumer {
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ITransactionService transactionService;
	@Autowired
	private TransactionProducer producer;

	@KafkaListener(topics = "created-withdrawal-topic", groupId = "transaction-group")
	public Disposable retrieveCreatedWithdrawal(String data) throws Exception {
		Withdrawal withdrawal = objectMapper.readValue(data, Withdrawal.class);
		return Mono.just(withdrawal)
				.map(w->{
					return transactionService
							.create(Transaction.builder()
									.account(w.getAccount())
									.description(w.getDescription())
									.transactionType("RETIRO")
									.transactionAmount(w.getAmount())
									.build())
							.block();
				})
				.flatMap(a->{
					producer.sendCreatedTransactionTopic(a);
					return Mono.just(a);
				})
				.subscribe();
	}
}

