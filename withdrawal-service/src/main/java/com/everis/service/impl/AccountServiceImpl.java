package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.Account;
import com.everis.repository.IAccountRepository;
import com.everis.repository.IRepository;
import com.everis.service.IAccountService;

@Service
public class AccountServiceImpl extends CRUDServiceImpl<Account, String> implements IAccountService{
	
	@Autowired
	private IAccountRepository repository;
	
	@Override
	protected IRepository<Account, String> getRepository() {
		return repository;
	}

}
