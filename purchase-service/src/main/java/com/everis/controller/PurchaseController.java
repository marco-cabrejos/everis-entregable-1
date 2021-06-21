package com.everis.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;

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
import com.everis.model.Customer;
import com.everis.model.Purchase;
import com.everis.service.ICustomerService;
import com.everis.service.IProductService;
import com.everis.service.IPurchaseService;
import com.everis.topic.producer.PurchaseProducer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/purchase")
public class PurchaseController {

	@Autowired
	private IPurchaseService service;
	@Autowired
	private ICustomerService customerService;
	@Autowired
	private IProductService productService;
	
	@Autowired
	private PurchaseProducer producer;
		
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
	public Mono<ResponseEntity<Response>> create(@RequestBody Purchase purchase, final ServerHttpRequest request){
		Mono<Purchase> monoPurchase = Mono.just(purchase);
		Flux<Customer> fluxCustomer = Flux.fromIterable(purchase.getCustomerOwner());
		return fluxCustomer
		.flatMap(p->customerService.findByIdentityNumber(p.getIdentityNumber()))
		.collectList()
		.flatMap(list->{
			list.stream().filter(Objects::nonNull).filter(c->c.getId()!=null);
			if(list.size()!=purchase.getCustomerOwner().size()) {
				return Mono.just(ResponseEntity
						.badRequest()
						.body(Response.builder().error("El cliente ingresado no existe.").build()));
			}
			return Mono.just(ResponseEntity
					.ok()
					.body(Response.builder().data(list).build()));
		});
		/*return service.create(purchase)
				.map(createdObject -> ResponseEntity
						.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(createdObject));
			*/	
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
				.flatMap(objectUpdated -> {
					producer.send(objectUpdated);
					return Mono.just(ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(objectUpdated));
				})
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
