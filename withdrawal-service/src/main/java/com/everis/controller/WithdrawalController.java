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

import com.everis.dto.Response;
import com.everis.model.Withdrawal;
import com.everis.service.IAccountService;
import com.everis.service.IWithdrawalService;
import com.everis.topic.producer.WithdrawalProducer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/withdrawal")
public class WithdrawalController {
	@Autowired
	private IWithdrawalService service;
	@Autowired
	private IAccountService accountService;
	
	@Autowired
	private WithdrawalProducer withdrawalProducer;
	
	@PostMapping
	public Mono<ResponseEntity<Response>> create(@RequestBody Withdrawal body, final ServerHttpRequest request){
		return accountService.findByAccountNumber(body.getAccount().getAccountNumber())
				.flatMap(account->{
					if(body.getAmount()>account.getCurrentBalance()) {
						return Mono.just(ResponseEntity
								.badRequest()
								.body(Response
										.builder()
										.error("El monto a retirar excede al saldo disponible")
										.build()));
					}
					account.setCurrentBalance(account.getCurrentBalance()-body.getAmount());
					body.setAccount(account);
					return service.create(body).flatMap(created->{
						withdrawalProducer.sendWithdrawalAccountTopic(body);
						return Mono.just(ResponseEntity
								.ok()
								.contentType(MediaType.APPLICATION_JSON)
								.body(Response.builder().data(body).build()));
					});
				})
				.defaultIfEmpty(ResponseEntity
						.badRequest()
						.body(Response
								.builder()
								.error("No es posible realizar el retiro, el número de cuenta no existe")
								.build()));
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

