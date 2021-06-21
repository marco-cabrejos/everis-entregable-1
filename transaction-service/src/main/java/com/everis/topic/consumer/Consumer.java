package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.CreditConsumer;
import com.everis.model.CreditPayment;
import com.everis.model.Deposit;
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
				.map(w -> {
					return transactionService
							.create(Transaction.builder()
									.account(w.getAccount())
									.purchase(w.getPurchase())
									.description(w.getDescription())
									.transactionType("RETIRO")
									.transactionAmount(w.getAmount())
									.transactionDate(w.getWithdrawalDate())
									.build())
							.block();
				})
				.flatMap(a -> {
					producer.sendCreatedTransactionAccountTopic(a);
					return Mono.just(a);
				})
				.subscribe();
		
	}

	@KafkaListener(topics = "created-deposit-topic", groupId = "transaction-group")
	public Disposable retrieveCreatedDeposit(String data) throws Exception {
		
		Deposit deposit = objectMapper.readValue(data, Deposit.class);
		
		return Mono.just(deposit)
				.map(d -> {
					return transactionService
							.create(Transaction.builder()
									.account(d.getAccount())
									.purchase(d.getPurchase())
									.description(d.getDescription())
									.transactionType("DEPOSITO")
									.transactionAmount(d.getAmount())
									.transactionDate(d.getDepositDate())
									.build())
							.block();
				})
				.flatMap(a -> {
					producer.sendCreatedTransactionAccountTopic(a);
					return Mono.just(a);
				})
				.subscribe();
		
	}

	@KafkaListener(topics = "created-credit-consumer-topic", groupId = "transaction-group")
	public Disposable retrieveCreatedCreditConsumer(String data) throws Exception {
		
		CreditConsumer creditConsumer = objectMapper.readValue(data, CreditConsumer.class);
		
		return Mono.just(creditConsumer)
				.map(c -> {
					return transactionService
							.create(Transaction.builder()
									.purchase(c.getPurchase())
									.description(c.getDescription())
									.transactionType("CONSUMO TARJETA CREDITO")
									.transactionAmount(c.getAmount())
									.transactionDate(c.getConsumDate())
									.build())
							.block();
				})
				.flatMap(a -> {
					return Mono.just(a);
				})
				.subscribe();
		
	}

	@KafkaListener(topics = "created-credit-payment-topic", groupId = "transaction-group")
	public Disposable retrieveCreatedCreditPayment(String data) throws Exception {
		
		CreditPayment creditPayment = objectMapper.readValue(data, CreditPayment.class);
		
		return Mono.just(creditPayment)
				.map(p -> {
					return transactionService
							.create(Transaction.builder()
									.purchase(p.getPurchase())
									.description(p.getDescription())
									.transactionType("PAGO TARJETA CREDITO")
									.transactionAmount(p.getAmount())
									.transactionDate(p.getConsumDate())
									.build())
							.block();
				})
				.flatMap(a -> {
					return Mono.just(a);
				})
				.subscribe();
		
	}
	
}

