package com.everis.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "purchase")
@Data
public class Purchase {

	@Id
	private String id;

	@Field(name = "product")
	private Product product;

	@Field(name = "customerOwner")
	private List<Customer> customerOwner;

	@Field(name = "authorizedSigner")
	private List<Customer> authorizedSigner;
	
	@Field(name = "amount")
	private double amount;
	
	@Field(name = "purchaseDate")
	private Date purchaseDate;
	
}
