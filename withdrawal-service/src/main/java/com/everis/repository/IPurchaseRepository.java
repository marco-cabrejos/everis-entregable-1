package com.everis.repository;

import com.everis.model.Purchase;

import reactor.core.publisher.Mono;

public interface IPurchaseRepository extends IRepository<Purchase, String> {

	Mono<Purchase> findByCardNumber(String cardNumber);
	
}
