package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.Customer;
import com.everis.repository.ICustomerRepository;
import com.everis.repository.IRepository;
import com.everis.service.ICustomerService;

import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl extends CRUDServiceImpl<Customer, String> implements ICustomerService {

	@Autowired
	private ICustomerRepository repository;

	@Override
	protected IRepository<Customer, String> getRepository() {

		return repository;

	}

	@Override
	public Mono<Customer> findByIdentityNumber(String identityNumber) {
		return repository.findByIdentityNumber(identityNumber);
	}

}
