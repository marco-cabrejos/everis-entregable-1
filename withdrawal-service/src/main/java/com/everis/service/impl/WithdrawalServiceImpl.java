package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.Withdrawal;
import com.everis.repository.IRepository;
import com.everis.repository.IWithdrawalRepository;
import com.everis.service.IWithdrawalService;

@Service
public class WithdrawalServiceImpl extends CRUDServiceImpl<Withdrawal, String> implements IWithdrawalService {

	@Autowired
	private IWithdrawalRepository repository;

	@Override
	protected IRepository<Withdrawal, String> getRepository() {
		return repository;
	}

}
