package com.tc.banking.atm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.tc.banking.atm.entity.AccountEntity;
import com.tc.banking.atm.exception.ActionNotPermittedException;
import com.tc.banking.atm.exception.InvalidAccountNumberException;
import com.tc.banking.atm.repository.AccountRepository;
import com.tc.banking.atm.request.WithdrawalRequest;
import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceTest {

	private static final String NOTE_NAME = "AnyNote";
	private static final int NUMBER_NOTES = 5;
	private static final Integer PIN = 1234;
	private static final Integer ACCOUNT = 123456789;
	private static final double BALANCE = 100.0;
	private static final double OVERDRAFT = 50.0;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private BankNoteService bankNoteService;

	@InjectMocks
	private AccountService accountService;

	@BeforeEach
	void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	void testRetrieveFundsIsSuccessful() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		AccountCheckResponse response = accountService.retrieveFunds(PIN, ACCOUNT);
		assertEquals(response.getAvailableFunds(), 150.00 + "");
		assertEquals(response.getBalance(), 100.00 + "");
	}

	@Test
	void testAccountNotFoundThrowsInvalidAccountException() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(null);
		Exception exception = assertThrows(InvalidAccountNumberException.class, () -> {
			accountService.retrieveFunds(PIN, ACCOUNT);
		});
		String expectedMessage = "Account does not exist";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testIncorrectPinThrowsAtmException() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		Exception exception = assertThrows(ActionNotPermittedException.class, () -> {
			accountService.retrieveFunds(0000, ACCOUNT);
		});
		String expectedMessage = "Invalid pin number entered";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testWithdrawFundsIsSuccessful() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(200.0);

		WithdrawCashResponse response = accountService.withdrawFunds(new WithdrawalRequest(PIN, ACCOUNT, 10.0));
		assertEquals(90.0, response.getNewBalance(), 10.0);
		assertEquals(1, response.getNotes().size());
		assertEquals(NOTE_NAME, response.getNotes().get(0).getNote());
		assertEquals(NUMBER_NOTES, response.getNotes().get(0).getNumberOfNotes());

	}

	@Test
	void testUserBalanceInsufficient() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(200.0);
		WithdrawalRequest request = new WithdrawalRequest(PIN, ACCOUNT, 160.0);
		Exception exception = assertThrows(ActionNotPermittedException.class, () -> {
			accountService.withdrawFunds(request);
		});
		String expectedMessage = "Insufficient funds";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void testAtmBalanceInsufficient() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(50.0);

		WithdrawalRequest request = new WithdrawalRequest(PIN, ACCOUNT, 80.0);
		Exception exception = assertThrows(ActionNotPermittedException.class, () -> {
			accountService.withdrawFunds(request);
		});
		String expectedMessage = "Unable to dispense this amount - Choose a lower amount";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void testUserCanUseOverdraft() {
		when(accountRepository.findByAccountNumber(anyInt())).thenReturn(getAccountEntity());
		when(bankNoteService.retrieveRequestedAmount(anyDouble())).thenReturn(getBankNotesList());
		when(bankNoteService.checkAtmBalance()).thenReturn(200.0);

		WithdrawCashResponse response = accountService.withdrawFunds(new WithdrawalRequest(PIN, ACCOUNT, 120.0));
		assertEquals(0, response.getNewBalance(), 100.0);
		assertEquals(1, response.getNotes().size());
		assertEquals(NOTE_NAME, response.getNotes().get(0).getNote());
		assertEquals(NUMBER_NOTES, response.getNotes().get(0).getNumberOfNotes());
	}

	private AccountEntity getAccountEntity() {
		AccountEntity entity = new AccountEntity();
		entity.setAccountNumber(ACCOUNT);
		entity.setBalance(BALANCE);
		entity.setOverdraft(OVERDRAFT);
		entity.setPin(PIN);
		return entity;
	}

	private List<BankNoteResponse> getBankNotesList() {
		List<BankNoteResponse> bankNoteList = new ArrayList<>();

		BankNoteResponse bankNote = new BankNoteResponse();
		bankNote.setNote(NOTE_NAME);
		bankNote.setNumberOfNotes(NUMBER_NOTES);
		bankNoteList.add(bankNote);
		return bankNoteList;
	}

}
