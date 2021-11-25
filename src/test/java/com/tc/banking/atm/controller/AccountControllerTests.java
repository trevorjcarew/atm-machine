package com.tc.banking.atm.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tc.banking.atm.response.AccountCheckResponse;
import com.tc.banking.atm.response.BankNoteResponse;
import com.tc.banking.atm.response.WithdrawCashResponse;
import com.tc.banking.atm.service.AccountService;


public class AccountControllerTests {

    private MockMvc mockMvc;
	
	@Mock
    private AccountService accountService;
    
    @InjectMocks
    private AccountController accountController;
    
    @BeforeEach
	void init(){
	    MockitoAnnotations.initMocks(this);
	    mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
	}
    
//    @Before
//    public void setup() {
//    	MockitoAnnotations.initMocks(this);
//    	mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
//    }

    @Test
    void testGetFunds() throws Exception {
        when(accountService.retrieveFunds(anyInt(), anyInt())).thenReturn(getAccountCheckResponse());
        mockMvc.perform(get("http://localhost:8080/atm/checkFunds?pin=4321&accountNumber=987654321")
        		.contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("balance", is("10")))
        .andExpect(jsonPath("availableFunds", is("15")))
        .andExpect(status().isOk());
        	
    }
    
    @Test
    void testWithdrawFunds() throws Exception {
        when(accountService.withdrawFunds(any())).thenReturn(getWithdrawCashResponse());
        JSONObject body = new JSONObject();
		body.put("accountNumber", 987654321);
		body.put("pin", 4321);
		body.put("amount", 1100);
//        mockMvc.perform(put("http://localhost:8080/atm/withdrawFunds?pin=4321&accountNumber=987654321&amount=1100")
//        		.contentType(MediaType.APPLICATION_JSON))
        mockMvc.perform(put("http://localhost:8080/atm/withdrawFunds").content(body.toString())
        		.contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("newBalance", is(22.0)))
        .andExpect(status().isOk());
        	
    }
    
    private AccountCheckResponse getAccountCheckResponse() {
    	AccountCheckResponse response = new AccountCheckResponse();
    	response.setBalance("10");
    	response.setAvailableFunds("15");
    	return response;
    }
    
    private WithdrawCashResponse getWithdrawCashResponse() {
    	WithdrawCashResponse response = new WithdrawCashResponse();
    	response.setNewBalance(22.0);
    	response.setNotes(getBankNoteResponse());
    	return response;
    }
    
    private List<BankNoteResponse> getBankNoteResponse() {
    	List<BankNoteResponse> response = new ArrayList<BankNoteResponse>();
    	BankNoteResponse note = new BankNoteResponse();
    	note.setNote("note");
    	note.setNumberOfNotes(5);
    	response.add(note);
    	return response;
    }
    

}
