package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Customer;
import com.everis.model.Product;
import com.everis.model.Purchase;
import com.everis.model.Transaction;
import com.everis.service.ICustomerService;
import com.everis.service.IProductService;
import com.everis.service.IPurchaseService;
import com.everis.topic.producer.PurchaseProducer;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class Consumer {
	
	@Autowired
	private IProductService productService;
	
	@Autowired
	private ICustomerService customerService;
	
	@Autowired
	private IPurchaseService purchaseService;
	
	@Autowired
	private PurchaseProducer producer;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@KafkaListener(topics = "saved-product-topic", groupId = "purchase-group")
	public Disposable retrieveSavedProduct(String data) throws Exception {
			
		Product product = objectMapper.readValue(data, Product.class);
		
		return Mono.just(product)
				.log()
				.flatMap(productService::update)
				.subscribe();
		
	}
	
	@KafkaListener(topics = "saved-customer-topic", groupId = "purchase-group")
	public Disposable retrieveSavedCustomer(String data) throws Exception {
		
		Customer customer = objectMapper.readValue(data, Customer.class);
		
		return Mono.just(customer)
				.log()
				.flatMap(customerService::update)
				.subscribe();
		
	}
	
	@KafkaListener(topics = "created-transaction-topic", groupId = "purchase-group")
	public Disposable retrieveCreatedTransaction(String data) throws Exception {
		
		Transaction transaction = objectMapper.readValue(data, Transaction.class);
		
		Mono<Purchase> monoPurchase = purchaseService.findById(transaction.getPurchase().getId());
		
		Mono<Transaction> monoTransaction = Mono.just(transaction);
		
		return monoPurchase
				.zipWith(monoTransaction, (a,b) -> {
					if(transaction.getTransactionType().equals("CONSUMO TARJETA CREDITO")) {
						a.setAmountFin(a.getAmountFin() - transaction.getTransactionAmount());
					} else if (transaction.getTransactionType().equals("PAGO TARJETA CREDITO")) {
						a.setAmountFin(a.getAmountIni());						
					}
					producer.send(a);
					return a;
				})
				.flatMap(purchaseService::update)
				.subscribe();
		
	}
	
}
