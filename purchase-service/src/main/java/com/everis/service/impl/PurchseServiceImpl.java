package com.everis.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.everis.model.Purchase;
import com.everis.repository.IPurchaseRepository;
import com.everis.repository.IRepository;
import com.everis.service.IPurchaseService;

@Service
public class PurchseServiceImpl extends CRUDServiceImpl<Purchase, String> implements IPurchaseService {

	@Autowired
	private IPurchaseRepository repository;
	
	@Override
	protected IRepository<Purchase, String> getRepository() {
		
		return repository;
		
	}

}
