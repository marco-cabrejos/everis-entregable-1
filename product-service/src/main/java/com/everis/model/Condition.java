package com.everis.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Condition {
	private List<String> customerTypeTarget;
	
	private boolean hasMaintenanceFee;
	private Double maintenanceFee;
	
	private boolean hasMonthlyTransactionLimit;
	private Integer monthlyTransactionLimit;
	
	private boolean hasDailyWithdrawalTransactionLimit;
	private Integer dailyWithdrawalTransactionLimit;
	
	private boolean hasDailyDepositTransactionLimit;
	private Integer dailyDepositTransactionLimit;
	
	/*private Integer creditPerPersonLimit;
	private Integer creditPerBusinessLimit;*/
	
	private Integer productPerPersonLimit;
	private Integer productPerBusinessLimit; 
}
