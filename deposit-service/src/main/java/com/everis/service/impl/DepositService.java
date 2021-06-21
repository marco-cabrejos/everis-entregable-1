package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.Deposit;
import com.everis.repository.IDepositRepository;
import com.everis.repository.IRepository;
import com.everis.service.IDepositService;

@Service
public class DepositService extends CRUDServiceImpl<Deposit, String> implements IDepositService {

	@Autowired
	private IDepositRepository repository;
	
	@Override
	protected IRepository<Deposit, String> getRepository() {
		
		return repository;
		
	}

}
