package com.everis.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "account")
@Data
public class Account {

	@Id
	private String id;
	
	@Field(name = "accountNumber")
	private String accountNumber;
	
	@Field(name = "dateOpened")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateOpened = LocalDateTime.now();
	
	@Field(name = "dateClosed")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dateClosed = null;
	
	@Field(name = "purchase")
	private Purchase purchase = new Purchase();
	
	@Field(name = "currentBalance")
	private double currentBalance;
	
	@Field(name = "maintenance_charge")
	private double maintenance_charge;
	
	@Field(name = "limitMovementsMonth")
	private int limitMovementsMonth;
	
	@Field(name = "dateMovement")
	private int dateMovement;
	
}
