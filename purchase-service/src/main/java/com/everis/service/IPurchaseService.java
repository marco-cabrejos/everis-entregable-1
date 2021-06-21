package com.everis.service;

import java.util.List;

import com.everis.model.Customer;
import com.everis.model.Purchase;

import reactor.core.publisher.Flux;

public interface IPurchaseService extends ICRUDService<Purchase, String> {
	Flux<Purchase> findByCustomerOwner(List<Customer> list);
}
