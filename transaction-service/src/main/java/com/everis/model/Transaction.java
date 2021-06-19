package com.everis.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Builder;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(collection = "transaction")
@Data
@Builder
public class Transaction {
	@Id
	private String id;

	@Field(name = "transactionType")
	private String transactionType;

	@Field(name = "transactionAmount")
	private Double transactionAmount;

	@Field(name = "account")
	private Account account;

	@Field(name = "description")
	private String description;

	@Builder.Default
	@Field(name = "transactionDate")
	private LocalDateTime transactionDate = LocalDateTime.now();
}
