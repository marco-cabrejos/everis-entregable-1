package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.CreditPayment;
import com.everis.repository.ICreditPaymentRepository;
import com.everis.repository.IRepository;
import com.everis.service.ICreditPaymentService;

@Service
public class CreditPaymentService extends CRUDServiceImpl<CreditPayment, String> implements ICreditPaymentService {

	@Autowired
	private ICreditPaymentRepository repository;
	
	@Override
	protected IRepository<CreditPayment, String> getRepository() {
		
		return repository;
		
	}
	
}
