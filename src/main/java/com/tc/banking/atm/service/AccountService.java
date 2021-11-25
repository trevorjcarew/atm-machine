package com.tc.banking.atm.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tc.banking.atm.entity.AccountEntity;
import com.tc.banking.atm.exception.ActionNotPermittedException;
import com.tc.banking.atm.exception.InvalidAccountNumberException;
import com.tc.banking.atm.repository.AccountRepository;
import com.tc.banking.atm.request.WithdrawalRequest;
import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private BankNoteService bankNoteService;

	public AccountCheckResponse retrieveFunds(int pin, int accountNumber) {
		AccountCheckResponse response = new AccountCheckResponse();
		AccountEntity entity = retrieveAccount(accountNumber);
		validatePin(pin, entity.getPin());
		response.setBalance(entity.getBalance() + "");
		response.setAvailableFunds(entity.getBalance() + entity.getOverdraft() + "");
		return response;
	}

	public WithdrawCashResponse withdrawFunds(WithdrawalRequest request) {
		AccountEntity accountEntity = retrieveAccount(request.getAccountNumber());
		validatePin(request.getPin(), accountEntity.getPin());
		Double amount = request.getAmount();
		verifyUserBalance(amount, accountEntity.getBalance() + accountEntity.getOverdraft());
		verifyAtmBalance(amount);
		List<BankNoteResponse> retrievedNotes = bankNoteService.retrieveRequestedAmount(amount);
		double newBalance = updateAccountAndGetBalance(accountEntity, amount);
		return formWithdrawelResponse(retrievedNotes, newBalance);
	}

	private double updateAccountAndGetBalance(AccountEntity accountEntity, double amount) {
		double balance = accountEntity.getBalance();
		double overdraft = accountEntity.getOverdraft();

		// when overdrawn set balance to 0 and update overdraft amount
		if (amount > balance) {
			double overdraftReduction = amount - balance;
			accountEntity.setOverdraft(overdraft - overdraftReduction);
			accountEntity.setBalance(0.0);
		} else {
			accountEntity.setBalance(balance - amount);
		}
		accountRepository.save(accountEntity);

		return accountEntity.getBalance();
	}

	private AccountEntity retrieveAccount(int accountNumber) {
		AccountEntity entity = accountRepository.findByAccountNumber(accountNumber);
		if (entity == null) {
			log.error("accountNumber not found - " + accountNumber);
			throw new InvalidAccountNumberException("Account does not exist");
		}
		return entity;
	}

	private WithdrawCashResponse formWithdrawelResponse(List<BankNoteResponse> retrievedNotes, double newBalance) {
		WithdrawCashResponse response = new WithdrawCashResponse();

		response.setNewBalance(newBalance);
		List<BankNoteResponse> nonZeroBankNotes = new ArrayList<>();

		// remove notes with a quantity of 0 from response
		for (int i = 0; i < retrievedNotes.size(); i++) {
			if (retrievedNotes.get(i).getNumberOfNotes() != 0) {
				nonZeroBankNotes.add(retrievedNotes.get(i));
			}
		}
		response.setNotes(nonZeroBankNotes);

		return response;
	}

	private void verifyAtmBalance(double requestedAmount) {
		// get all notes from the db and sum up their total
		Double atmBalance = bankNoteService.checkAtmBalance();
		if (requestedAmount > atmBalance) {
			log.error("requestedAmout - " + requestedAmount + ", atmBalanace - " + atmBalance);
			throw new ActionNotPermittedException("Unable to dispense this amount - Choose a lower amount");
		}
	}

	private void validatePin(int pin, int entityPin) {
		// check if pin provided matches pin in db
		if (pin != entityPin) {
			log.error("pin number entered doesn't match account pin");
			throw new ActionNotPermittedException("Invalid pin number entered");
		}
	}

	private void verifyUserBalance(double amount, double availableFunds) {
		// check that the have enough funds in their account
		if (amount > availableFunds) {
			log.error("requestedAmount - " + amount + ", availableFunds - " + availableFunds);
			throw new ActionNotPermittedException("Insufficient funds");
		}
	}
}
