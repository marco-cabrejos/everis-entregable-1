package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.CreditConsumer;
import com.everis.repository.ICreditConsumerRepository;
import com.everis.repository.IRepository;
import com.everis.service.ICreditConsumerService;

@Service
public class CreditConsumerService extends CRUDServiceImpl<CreditConsumer, String> implements ICreditConsumerService {

	@Autowired
	private ICreditConsumerRepository repository;
	
	@Override
	protected IRepository<CreditConsumer, String> getRepository() {
		
		return repository;
		
	}
	
}
