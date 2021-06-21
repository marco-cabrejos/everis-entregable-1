package com.everis.controller;

import java.net.URI;
import java.time.LocalDateTime;
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
import com.everis.model.Account;
import com.everis.service.IAccountService;
import com.everis.topic.producer.AccountProducer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private IAccountService service;
	
	@Autowired
	private AccountProducer producer;
	
	@GetMapping("/welcome")
	public Mono<ResponseEntity<String>> welcome(){
	
		return Mono.just(ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body("Welcome Account"));
		
	}
	
	@GetMapping
	public Mono<ResponseEntity<List<Account>>> findAll(){ 
		
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
	public Mono<ResponseEntity<Account>> findById(@PathVariable("id") String id){
		
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
	public Mono<ResponseEntity<Response>> create(@RequestBody Account account, final ServerHttpRequest request){
		
		return service.findAll().filter(list -> list.getAccountNumber().equals(account.getAccountNumber()))
				.collectList()
				.flatMap(list -> {
					return list.size() > 0 ?							
							Mono.just(ResponseEntity
								.badRequest()
								.body(Response
										.builder()
										.data("La cuenta " + account.getAccountNumber() + " ya existe")
										.build())) :							
							service.create(account)
								.flatMap(createdObject -> {
									producer.sendCreatedAccount(createdObject);
									return Mono.just(ResponseEntity
											.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
											.contentType(MediaType.APPLICATION_JSON)
											.body(Response
													.builder()
													.data(createdObject)
													.build()));
								});
				});
		
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Account>> update(@RequestBody Account account, @PathVariable("id") String id){
		
		Mono<Account> customerModification = Mono.just(account);
		
		Mono<Account> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerModification, (a,b) -> {
					a.setMaintenance_charge(account.getMaintenance_charge());
					a.setLimitMovementsMonth(account.getLimitMovementsMonth());
					a.setDateMovement(account.getDateMovement());
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
	public Mono<ResponseEntity<Response>> delete(@PathVariable("id") String id){
		
		Mono<Account> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerDatabase, (a,b) -> {
					a.setDateClosed(LocalDateTime.now());
					return a;
				})
				.flatMap(service::update)
				.map(objectUpdated -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(Response
								.builder()
								.data("Cuenta eliminada")
								.build()))
				.defaultIfEmpty(ResponseEntity
						.badRequest()
						.body(Response
								.builder()
								.data("La cuenta no existe")
								.build()));
		
	}
	
}