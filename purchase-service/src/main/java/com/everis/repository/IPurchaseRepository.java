package com.everis.repository;

import java.util.List;

import com.everis.model.Customer;
import com.everis.model.Purchase;

import reactor.core.publisher.Flux;

public interface IPurchaseRepository extends IRepository<Purchase, String> {
	Flux<Purchase> findByCustomerOwner(List<Customer> list);
}
