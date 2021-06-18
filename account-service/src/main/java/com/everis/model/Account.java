package com.everis.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
	private Date dateOpened;
	
	@Field(name = "dateClosed")
	private Date dateClosed;
	
	@Field(name = "purchase")
	private Purchase purchase;
	
	@Field(name = "currentBalance")
	private double currentBalance;
	
	@Field(name = "maintenance_charge")
	private double maintenance_charge;
	
	@Field(name = "limitMovementsMonth")
	private int limitMovementsMonth;
	
	@Field(name = "dateMovement")
	private int dateMovement;
	
}
