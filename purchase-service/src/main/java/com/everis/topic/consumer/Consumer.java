package com.everis.topic.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.everis.model.Customer;
import com.everis.model.Product;
import com.everis.service.ICustomerService;
import com.everis.service.IProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Component
public class Consumer {
	@Autowired
	private IProductService productService;
	@Autowired
	private ICustomerService customerService;
	
	ObjectMapper objectMapper = new ObjectMapper();
	
	@KafkaListener(topics = "saved-product-topic", groupId = "purchase-group")
	public Disposable retrieveSavedProduct(String data) throws Exception{
		System.out.println("procesando "+data);
		Product p = objectMapper.readValue(data, Product.class);
		return Mono.just(p)
				.log()
				.flatMap(productService::update)
				.subscribe();
	}
	
	@KafkaListener(topics = "saved-customer-topic", groupId = "purchase-group")
	public Disposable retrieveSavedCustomer(String data) throws Exception{
		System.out.println("procesando "+data);
		Customer p = objectMapper.readValue(data, Customer.class);
		return Mono.just(p)
				.log()
				.flatMap(customerService::update)
				.subscribe();
	}
}
