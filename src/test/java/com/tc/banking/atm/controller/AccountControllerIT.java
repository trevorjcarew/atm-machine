package com.tc.banking.atm.controller;

import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class AccountControllerIT {
	
	@LocalServerPort
    private int port;
	
	TestRestTemplate restTemplate = new TestRestTemplate();
	
	HttpHeaders headers = new HttpHeaders();
	
	@Test
	@Order(1)
	void testCheckFundsSuccess() throws Exception {
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
          createURLWithPort("/atm/checkFunds?pin=1234&accountNumber=123456789"), HttpMethod.GET, entity, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        JSONObject obj = new JSONObject(response.getBody());
        assertEquals("800.0", obj.get("balance"));
        assertEquals("1000.0", obj.get("availableFunds"));
        
	}
	
	@Test
	@Order(2)
	void testCheckFundsIncorrectPin() throws Exception {
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
          createURLWithPort("/atm/checkFunds?pin=9999&accountNumber=123456789"), HttpMethod.GET, entity, String.class);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        JSONObject obj = new JSONObject(response.getBody());
        assertEquals("Invalid pin number entered", obj.get("error"));        
        
	}
	
	@Test
	@Order(3)
	void testCheckFundsAccountNotFound() throws Exception {
		
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
          createURLWithPort("/atm/checkFunds?pin=9999&accountNumber=333333333"), HttpMethod.GET, entity, String.class);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        JSONObject obj = new JSONObject(response.getBody());
        assertEquals("Account does not exist", obj.get("error"));        
	}
	
	@Test
	@Order(4)
	void testWithdrawCompletesSuccessfully() throws Exception {
		
		JSONObject body = new JSONObject();
		body.put("accountNumber", 123456789);
		body.put("pin", 1234);
		body.put("amount", 1000);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
          createURLWithPort("/atm/withdrawFunds"), HttpMethod.PUT, entity, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        JSONObject responseBody = new JSONObject(response.getBody());
        assertEquals(0.0, responseBody.get("newBalance"));
        
        JSONArray notes = responseBody.getJSONArray("notes");
        JSONObject row = notes.getJSONObject(0);
        assertEquals("Fifty Euro Note", row.get("note"));
        assertEquals(20, row.get("numberOfNotes"));
        
	}
	
	@Test
	@Order(5)
	void testInsufficientBalance() throws Exception {
		
		JSONObject body = new JSONObject();
		body.put("accountNumber", 123456789);
		body.put("pin", 1234);
		body.put("amount", 50);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
          createURLWithPort("/atm/withdrawFunds"), HttpMethod.PUT, entity, String.class);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        JSONObject obj = new JSONObject(response.getBody());
        assertEquals("Insufficient funds", obj.get("error"));
        
	}
	
	@Test
	@Order(6)
	void testNotEnoughCashInATM() throws Exception {
		
		JSONObject body = new JSONObject();
		body.put("accountNumber", 987654321);
		body.put("pin", 4321);
		body.put("amount", 1380);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
          createURLWithPort("/atm/withdrawFunds"), HttpMethod.PUT, entity, String.class);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        JSONObject obj = new JSONObject(response.getBody());
        assertEquals("Unable to dispense this amount - Choose a lower amount", obj.get("error"));        
	}
	
	private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
