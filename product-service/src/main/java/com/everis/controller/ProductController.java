package com.everis.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.everis.model.Product;
import com.everis.service.IProductService;
import com.everis.topic.producer.ProductProducer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private ProductProducer producer;
	@Autowired
	private IProductService service;
	@GetMapping
	public Mono<ResponseEntity<List<Product>>> findAll(){ 
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
	public Mono<ResponseEntity<Product>> findById(@PathVariable("id") String id){
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
	public Mono<ResponseEntity<Product>> create(@RequestBody Product product, final ServerHttpRequest request){
		return service.create(product)
				.map(createdObject->{
					producer.sendSavedProductTopic(createdObject);
					return ResponseEntity
							.created(URI.create(request.getURI().toString().concat(createdObject.getId())))
							.contentType(MediaType.APPLICATION_JSON)
							.body(createdObject);
				});
	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<Product>> update(@RequestBody Product product, @PathVariable("id") String id){
		Mono<Product> ProductModification = Mono.just(product);
		Mono<Product> ProductDatabase = service.findById(id);
		
		return ProductDatabase
				.zipWith(ProductModification, (a,b)->{
					a.setId(id);
					a.setProductName(product.getProductName());
					a.setProductType(product.getProductType());
					a.getCondition().setCustomerTypeTarget(product.getCondition().getCustomerTypeTarget());
					a.getCondition().setHasMaintenanceFee(product.getCondition().isHasMaintenanceFee());
					a.getCondition().setMaintenanceFee(product.getCondition().getMaintenanceFee());
					a.getCondition().setHasMonthlyTransactionLimit(product.getCondition().isHasMonthlyTransactionLimit());
					a.getCondition().setMonthlyTransactionLimit(product.getCondition().getMonthlyTransactionLimit());
					a.getCondition().setHasDailyWithdrawalTransactionLimit(product.getCondition().isHasDailyWithdrawalTransactionLimit());
					a.getCondition().setDailyWithdrawalTransactionLimit(product.getCondition().getDailyWithdrawalTransactionLimit());
					a.getCondition().setHasDailyDepositTransactionLimit(product.getCondition().isHasDailyDepositTransactionLimit());
					a.getCondition().setDailyDepositTransactionLimit(product.getCondition().getDailyDepositTransactionLimit());
					/*a.getCondition().setCreditPerPersonLimit(product.getCondition().getCreditPerPersonLimit());
					a.getCondition().setCreditPerBusinessLimit(product.getCondition().getCreditPerBusinessLimit());*/
					a.getCondition().setProductPerPersonLimit(product.getCondition().getProductPerPersonLimit());
					a.getCondition().setProductPerBusinessLimit(product.getCondition().getProductPerBusinessLimit());
					return a;
				})
				.flatMap(service::update)
				.map(objectUpdated->{
					producer.sendSavedProductTopic(objectUpdated);
					return ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(objectUpdated);
				})
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id){
		return service.findById(id)				
				.flatMap(p -> {
					return service.delete(p.getId())
							.then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)));					
				})
				.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NO_CONTENT));
	}
}
