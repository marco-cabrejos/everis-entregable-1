package com.everis.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Document(collection = "deposit")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Deposit {

	@Id
	private String id;
	
	@Field(name = "account")
	private Account account;
	
	@Field(name = "purchase")
	private Purchase purchase;
	
	@Field(name = "amount")
	private Double amount;

	@Field(name = "description")
	private String description;

	@Field(name = "depositDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime depositDate;

}