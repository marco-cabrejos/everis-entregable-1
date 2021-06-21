package com.everis.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.everis.model.Transaction;
import com.everis.service.ITransactionService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	 
	@Autowired
	private ITransactionService service;
	
	@GetMapping
	public Mono<ResponseEntity<List<Transaction>>> findAll(){ 
		
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
		
//	@GetMapping("/{accountNumber}")
//	public Mono<Object> findAllByAccountNumber(@PathVariable("accountNumber") String accountNumber){ 
//		
//		return service.findAll().filter(list -> list.getAccount().getAccountNumber().equals(accountNumber))
//				.collectList()
//				.flatMap(list -> {
//					return list.size() > 0 ? 
//							Mono.just(ResponseEntity
//									.ok()
//									.contentType(MediaType.APPLICATION_JSON)
//									.body(list)) :
//							Mono.just(ResponseEntity
//									.noContent()
//									.build());
//				});
//				
//	}
	
	@GetMapping("/{cardNumber}")
	public Mono<Object> findAllByCardNumber(@PathVariable("cardNumber") String cardNumber){ 
		
		return service.findAll().filter(list -> list.getPurchase().getCardNumber().equals(cardNumber))
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
	
}
