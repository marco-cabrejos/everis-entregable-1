package com.everis.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.Customer;
import com.everis.model.Purchase;
import com.everis.repository.IPurchaseRepository;
import com.everis.repository.IRepository;
import com.everis.service.IPurchaseService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PurchseServiceImpl extends CRUDServiceImpl<Purchase, String> implements IPurchaseService {

	@Autowired
	private IPurchaseRepository repository;
	
	@Override
	protected IRepository<Purchase, String> getRepository() {
		
		return repository;
		
	}

//	@Override
//	public Flux<Purchase> findByCustomerOwner(List<Customer> customers) {
//		
//		return repository.findByCustomerOwner(customers);
//		
//	}

	@Override
	public Mono<List<Purchase>> findByIdentityNumberAndProductID(String identityNumber, String idProduct) {
		return repository.findByIdentityNumberAndProductID(identityNumber,idProduct);
	}
	
}
