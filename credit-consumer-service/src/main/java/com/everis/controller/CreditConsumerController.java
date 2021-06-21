package com.everis.controller;

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
import com.everis.model.CreditConsumer;
import com.everis.service.ICreditConsumerService;
import com.everis.service.IPurchaseService;
import com.everis.topic.producer.CreditConsumerProducer;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/credit")
public class CreditConsumerController {

	@Autowired
	private ICreditConsumerService service;
	
	@Autowired
	private IPurchaseService purchaseService;
	
	@Autowired
	private CreditConsumerProducer creditConsumerProducer;

	@GetMapping
	public Mono<ResponseEntity<List<CreditConsumer>>> findAll() {
		
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
	public Mono<ResponseEntity<CreditConsumer>> findById(@PathVariable("id") String id) {
		
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
	public Mono<ResponseEntity<Response>> create(@RequestBody CreditConsumer creditConsumer, final ServerHttpRequest request) {
		
		return purchaseService.findByCardNumber(creditConsumer.getPurchase().getCardNumber())
				.flatMap(purchase -> {
					if(creditConsumer.getAmount() > purchase.getAmount()) {
						return Mono.just(ResponseEntity
								.badRequest()
								.body(Response
										.builder()
										.error("El monto a cobrar excede al saldo disponible")
										.build()));
					}
					purchase.setAmount(purchase.getAmount() - creditConsumer.getAmount());
					creditConsumer.setPurchase(purchase);
					return service.create(creditConsumer)
							.flatMap(created -> {
								creditConsumerProducer.sendCreditConsumerTransactionTopic(creditConsumer);
								return Mono.just(ResponseEntity
										.ok()
										.contentType(MediaType.APPLICATION_JSON)
										.body(Response
												.builder()
												.data(creditConsumer)
												.build()));
							});
				})
				.defaultIfEmpty(ResponseEntity
						.badRequest()
						.body(Response
								.builder()
								.error("No es posible realizar el cobro, la tarjeta no existe")
								.build()));
		
	}
	
}
