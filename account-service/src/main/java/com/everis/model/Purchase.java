package com.everis.model;

import java.util.List;

import lombok.Data;

@Data
public class Purchase {

	private Product product;

	private List<Customer> customerOwner;

	private List<Customer> authorizedSigner;
	
	private double amount;
		
}
