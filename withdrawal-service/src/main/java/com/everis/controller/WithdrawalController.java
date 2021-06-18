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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.model.Withdrawal;
import com.everis.service.IWithdrawalService;
import com.everis.topic.producer.WithdrawalProducer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/withdrawal")
public class WithdrawalController {
	@Autowired
	private IWithdrawalService service;
	@Autowired
	private WithdrawalProducer withdrawalProducer;
	
	@PostMapping
	public Mono<ResponseEntity<Withdrawal>> create(@RequestBody Withdrawal body, final ServerHttpRequest request){
		return service.create(body)
				.flatMap(p -> {
					withdrawalProducer.sendWithdrawalAccountTopic(p);
					return Mono.just(ResponseEntity
							.created(URI.create(request.getURI().toString().concat(body.getId())))
							.contentType(MediaType.APPLICATION_JSON)
							.body(p));
					});
	}
	
	@GetMapping
	public Mono<ResponseEntity<List<Withdrawal>>> findAll(){
		return service.findAll()
				.collectList()
				.flatMap(list->{
					return list.size()>0?Mono.just(ResponseEntity
							.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(list)):
								Mono.just(ResponseEntity.noContent().build());
				});
	}
	
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Withdrawal>> findById(@PathVariable("id") String id){
		return service.findById(id)
				.map(foundObject->ResponseEntity
						.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(foundObject))
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
}

