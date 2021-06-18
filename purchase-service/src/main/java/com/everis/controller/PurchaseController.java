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

import com.everis.model.Purchase;
import com.everis.service.IPurchaseService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

	@Autowired
	private IPurchaseService service;
	
	@GetMapping("/welcome")
	public Mono<ResponseEntity<String>> welcome(){
		 
		return Mono.just(ResponseEntity
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body("Welcome Purchase"));

	}
	
	@GetMapping
	public Mono<ResponseEntity<List<Purchase>>> findAll(){ 
		
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
	public Mono<ResponseEntity<Purchase>> findById(@PathVariable("id") String id){
		
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
	public Mono<ResponseEntity<Purchase>> create(@RequestBody Purchase customer, final ServerHttpRequest request){
		
		return service.create(customer)
				.map(createdObject -> ResponseEntity
						.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(createdObject));
				
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Purchase>> update(@RequestBody Purchase purchase, @PathVariable("id") String id){
		
		Mono<Purchase> customerModification = Mono.just(purchase);
		
		Mono<Purchase> customerDatabase = service.findById(id);
		
		return customerDatabase
				.zipWith(customerModification, (a,b) -> {
					a.setId(id);
					a.setProduct(purchase.getProduct());
					a.setCustomerOwner(purchase.getCustomerOwner());
					a.setAuthorizedSigner(purchase.getAuthorizedSigner());
					a.setAmount(purchase.getAmount());
					a.setPurchaseDate(purchase.getPurchaseDate());
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
						.body(""))
				.defaultIfEmpty(ResponseEntity
						.noContent()
						.build());
				
	}	 
	 
}
