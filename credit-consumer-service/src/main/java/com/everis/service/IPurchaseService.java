package com.everis.service;

import com.everis.model.Purchase;

import reactor.core.publisher.Mono;

public interface IPurchaseService extends ICRUDService<Purchase, String> {
	
	Mono<Purchase> findByCardNumber(String cardNumber);

}
