package com.everis.service;

import com.everis.model.Account;

import reactor.core.publisher.Mono;

public interface IAccountService extends ICRUDService<Account, String> {

	Mono<Account> findByAccountNumber(String accountNumber);
	
}
