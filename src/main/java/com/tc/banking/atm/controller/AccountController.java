package com.tc.banking.atm.controller;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tc.banking.atm.request.WithdrawalRequest;
import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;
import com.tc.banking.atm.service.AccountService;

@RestController
@RequestMapping("/atm")
@Validated
public class AccountController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/checkFunds")
	public ResponseEntity<AccountCheckResponse> checkFunds(
			@Valid @Positive(message = "incorrect input for input - pin") @RequestParam Integer pin,
			@Valid @Positive(message = "incorrect input for input - accountNumber") @RequestParam Integer accountNumber) {
		AccountCheckResponse response = accountService.retrieveFunds(pin, accountNumber);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/withdrawFunds")
	public ResponseEntity<WithdrawCashResponse> withdrawFunds(@Valid @RequestBody WithdrawalRequest request) {
		WithdrawCashResponse response = accountService.withdrawFunds(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
