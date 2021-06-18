package com.everis.controller;

import java.net.URI;
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

import com.everis.model.Account;
import com.everis.service.IAccountService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private IAccountService service;
	
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
	public Mono<ResponseEntity<Account>> create(@RequestBody Account account, final ServerHttpRequest request){
		
		return service.create(account)
				.map(createdObject -> ResponseEntity
						.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(createdObject));
				
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Account>> update(@RequestBody Account account, @PathVariable("id") String id){
		
		Mono<Account> customerModification = Mono.just(account);
		
		Mono<Account> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerModification, (a,b) -> {
					a.setId(id);
					a.setAccountNumber(account.getAccountNumber());
					a.setDateOpened(account.getDateOpened());
					a.setDateClosed(account.getDateClosed());
					a.setPurchase(account.getPurchase());
					a.setCurrentBalance(account.getCurrentBalance());
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
	public Mono<ResponseEntity<String>> delete(@PathVariable("id") String id){
		
		return service.delete(id)
				.map(objectDeleted -> ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body("Documento Eliminado"))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
				
	}
	
}