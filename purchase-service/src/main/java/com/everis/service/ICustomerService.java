package com.everis.service;

import com.everis.model.Customer;

import reactor.core.publisher.Mono;

public interface ICustomerService extends ICRUDService<Customer, String> {
	Mono<Customer> findByIdentityNumber(String identityNumber);
}
