package com.everis.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "purchase")
@Data
public class Purchase {
	
	@Id
	private String id;

	@Field(name = "cardNumber")
	private String cardNumber;

	@Field(name = "amount")
	private Double amount;

}
