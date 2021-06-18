package com.everis.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Customer {

	private String name;
	
	private String identityType;

	private String identityNumber;

	private String customerType;
	
}