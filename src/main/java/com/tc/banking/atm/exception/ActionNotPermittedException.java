package com.tc.banking.atm.exception;

public class ActionNotPermittedException extends RuntimeException {	
	
	private static final long serialVersionUID = 1L;

	public ActionNotPermittedException(String message) {
		super(message);			
	}

}
