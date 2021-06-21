package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.Transaction;
import com.everis.repository.IRepository;
import com.everis.repository.ITransactionRepository;
import com.everis.service.ITransactionService;

@Service
public class TransactionService extends CRUDServiceImpl<Transaction, String> implements ITransactionService {

	@Autowired
	private ITransactionRepository repository;

	@Override
	protected IRepository<Transaction, String> getRepository() {
		
		return repository;
		
	}

}
