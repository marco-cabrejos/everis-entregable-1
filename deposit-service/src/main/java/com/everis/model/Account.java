package com.everis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "account")
@Data
public class Account {
	
	@Id
	private String id;

	@Field(name = "accountNumber")
	private String accountNumber;

	@Field(name = "currentBalance")
	private Double currentBalance;
		
}
