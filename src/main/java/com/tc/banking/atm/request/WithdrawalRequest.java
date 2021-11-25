package com.tc.banking.atm.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawalRequest {
	
	@Positive(message = "invalid pin number")
	private Integer pin;
//	@NotNull
	@Positive(message = "invalid account number")
	private Integer accountNumber;
	@Positive(message = "invalid amount")
	private Double amount;

}
