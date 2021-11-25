package com.tc.banking.atm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.tc.banking.atm.entity.BankNoteEntity;
import com.tc.banking.atm.exception.ActionNotPermittedException;
import com.tc.banking.atm.repository.BankNoteRepository;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.util.BankNoteEnum;

@ExtendWith(MockitoExtension.class)
class BankNoteServiceTest {
	
	@Mock
	private BankNoteRepository bankNoteRepository;
	
	@InjectMocks
	private BankNoteService bankNoteService;
	
	@BeforeEach
	void init(){
	    MockitoAnnotations.initMocks(this);
	}
	
	@Test
	void testCheckAtmBalanceReturnsCorrectAmount() {
		when(bankNoteRepository.findAll()).thenReturn(getBankNoteEntityList());		
		double atmBalance = bankNoteService.checkAtmBalance();
		assertEquals(170.0, atmBalance, 0);
	}
	
	@Test
	void testRetrieveRequestedAmountIsSuccessful() {
		setupMockResponses();
		List<BankNoteResponse> response = bankNoteService.retrieveRequestedAmount(85.0);
		assertEquals(4, response.size());
		assertEquals("Fifty Euro Note", response.get(0).getNote());
		assertEquals("Twenty Euro Note", response.get(1).getNote());
		assertEquals("Ten Euro Note", response.get(2).getNote());
		assertEquals("Five Euro Note", response.get(3).getNote());
		assertEquals(1, response.get(0).getNumberOfNotes());
		assertEquals(1, response.get(1).getNumberOfNotes());
		assertEquals(1, response.get(2).getNumberOfNotes());
		assertEquals(1, response.get(3).getNumberOfNotes());		
	}
	
	@Test
	void testRetrieveRequestedAmountDoesntHaveSufficientAmoutOfNotes() {
		setupMockResponses();		
		Exception exception = assertThrows(ActionNotPermittedException.class, () -> {
			bankNoteService.retrieveRequestedAmount(180.0);
		});
		String expectedMessage = "Unable to dispense requested amount - Choose amount in different multiples";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test 
	void testRetrieveRequestedAmountProvidesMinNumberOfNotes() {
		setupMockResponses();
		List<BankNoteResponse> response = bankNoteService.retrieveRequestedAmount(70.0);
		assertEquals("Fifty Euro Note", response.get(0).getNote());
		assertEquals("Twenty Euro Note", response.get(1).getNote());
		assertEquals("Ten Euro Note", response.get(2).getNote());
		assertEquals("Five Euro Note", response.get(3).getNote());
		assertEquals(1, response.get(0).getNumberOfNotes());
		assertEquals(1, response.get(1).getNumberOfNotes());
		assertEquals(0, response.get(2).getNumberOfNotes());
		assertEquals(0, response.get(3).getNumberOfNotes());
	}
	
	private void setupMockResponses() {
		when(bankNoteRepository.findByName(BankNoteEnum.FIFTY.getName())).thenReturn(getNoteEntity(1, "Fifty", 50.0));
		when(bankNoteRepository.findByName(BankNoteEnum.TWENTY.getName())).thenReturn(getNoteEntity(2, "Twenty", 20.0));
		when(bankNoteRepository.findByName(BankNoteEnum.TEN.getName())).thenReturn(getNoteEntity(1, "Ten", 10.0));
		when(bankNoteRepository.findByName(BankNoteEnum.FIVE.getName())).thenReturn(getNoteEntity(1, "Five", 5.0));
		
	}

	private List<BankNoteEntity> getBankNoteEntityList() {
		List<BankNoteEntity> bankNoteList = new ArrayList<>();
		bankNoteList.add(getNoteEntity(1, "Fifty", 50.0));
		bankNoteList.add(getNoteEntity(2, "Twenty", 20.0));
		bankNoteList.add(getNoteEntity(3, "Ten", 10.0));
		bankNoteList.add(getNoteEntity(4, "Five", 5.0));
		
		return bankNoteList;
	}
	
	private BankNoteEntity getNoteEntity(int id, String noteName, double value) {
		BankNoteEntity entity = new BankNoteEntity();
		entity.setId(id);
		entity.setName(noteName + " Euro Note");
		entity.setValue(value);
		entity.setQuantity(2);		
		return entity;
	}
}
