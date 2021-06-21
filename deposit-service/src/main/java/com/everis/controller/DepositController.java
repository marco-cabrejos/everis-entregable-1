package com.everis.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.dto.Response;
import com.everis.model.Deposit;
import com.everis.service.IAccountService;
import com.everis.service.IDepositService;
import com.everis.topic.producer.DepositProducer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/deposit")
public class DepositController {

	@Autowired
	private IDepositService service;
	
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private DepositProducer depositProducer;
	
	@GetMapping
	public Mono<ResponseEntity<List<Deposit>>> findAll() { 
		
		return service.findAll()
				.collectList()
				.flatMap(list -> {
					return list.size() > 0 ? 
							Mono.just(ResponseEntity
									.ok()
									.contentType(MediaType.APPLICATION_JSON)
									.body(list)) :
							Mono.just(ResponseEntity
									.noContent()
									.build());
				});
				
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Deposit>> findById(@PathVariable("id") String id) {
		
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
	public Mono<ResponseEntity<Response>> create(@RequestBody Deposit deposit, final ServerHttpRequest request) {
		
		return accountService.findByAccountNumber(deposit.getAccount().getAccountNumber())
				.flatMap(account -> {
					account.setCurrentBalance(account.getCurrentBalance() + deposit.getAmount());
					deposit.setAccount(account);
					return service.create(deposit)
							.flatMap(created -> {
								depositProducer.sendDepositAccountTopic(deposit);
								return Mono.just(ResponseEntity
										.ok()
										.contentType(MediaType.APPLICATION_JSON)
										.body(Response
												.builder()
												.data(deposit)
												.build()));
							});
				})
				.defaultIfEmpty(ResponseEntity
						.badRequest()
						.body(Response
								.builder()
								.error("No es posible realizar el retiro, el número de cuenta no existe")
								.build()));
		
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Deposit>> update(@RequestBody Deposit deposit, @PathVariable("id") String id) {
		
		Mono<Deposit> depositModification = Mono.just(deposit);
		
		Mono<Deposit> depositDatabase = service.findById(id);
		
		return depositDatabase
				.zipWith(depositModification, (a,b) -> {
					a.setAmount(deposit.getAmount());
					a.setAccount(deposit.getAccount());
					a.setDescription(deposit.getDescription());
					return a;
				})
				.flatMap(service::update)
				.map(objectUpdated -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(objectUpdated))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
		
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<String>> delete(@PathVariable("id") String id) {
		
		return service.delete(id)
				.map(objectDeleted -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(""))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
				
	}
	
}
