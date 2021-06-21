package com.everis.repository;

import com.everis.model.Account;

import reactor.core.publisher.Mono;

public interface IAccountRepository extends IRepository<Account, String> {

	Mono<Account> findByAccountNumber(String accountNumber);
	
}
