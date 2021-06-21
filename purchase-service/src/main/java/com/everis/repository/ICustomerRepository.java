package com.everis.repository;

import com.everis.model.Customer;

import reactor.core.publisher.Mono;

public interface ICustomerRepository extends IRepository<Customer, String> {
	Mono<Customer> findByIdentityNumber(String identityNumber);
}
