package com.everis.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.model.Customer;
import com.everis.service.ICustomerService;
import com.everis.topic.producer.CustomerProducer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/customer")
public class CustomerController {
	
	@Autowired
	private ICustomerService service;
	
	@Autowired
	private CustomerProducer producer;
	
	@GetMapping
	public Mono<ResponseEntity<List<Customer>>> findAll(){ 
		return service.findAll()
				.collectList()
				.flatMap(list->{
					return list.size()>0?Mono.just(ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(list)):
								Mono.just(ResponseEntity
										.noContent()
										.build());
				});
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Customer>> findById(@PathVariable("id") String id){
		return service.findById(id)
				.map(objectFound -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(objectFound))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Customer>> create(@RequestBody Customer customer, final ServerHttpRequest request){
		return service.create(customer)
				.map(createdObject->{
					producer.sendSavedCustomerTopic(createdObject);
					return ResponseEntity
							.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
							.contentType(MediaType.APPLICATION_JSON)
							.body(createdObject);
				});
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Customer>> update(@RequestBody Customer customer, @PathVariable("id") String id){
		Mono<Customer> customerModification = Mono.just(customer);
		Mono<Customer> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerModification, (a,b)->{
					a.setId(id);
					a.setName(customer.getName());
					a.setIdentityType(customer.getIdentityType());
					a.setIdentityNumber(customer.getIdentityNumber());
					a.setAddress(customer.getAddress());
					a.setPhoneNumber(customer.getPhoneNumber());
					return a;
				})
				.flatMap(service::update)
				.map(objectUpdated->{
					producer.sendSavedCustomerTopic(objectUpdated);
					return ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(objectUpdated);
				})
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
	
}
