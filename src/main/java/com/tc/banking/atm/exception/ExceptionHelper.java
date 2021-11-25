package com.tc.banking.atm.exception;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.tc.banking.atm.response.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionHelper {

	@ExceptionHandler(value = { NoSuchElementException.class })
	public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException ex) {
		log.error("No element exists - ", ex.getMessage());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.NOT_FOUND.getReasonPhrase());
		response.setStatusCode(HttpStatus.NOT_FOUND.value());
		response.setError(ex.getMessage());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);

	}
	
	@ExceptionHandler(value = { ConstraintViolationException.class })
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
		log.error("Invalid Input Exception: ", ex.getMessage());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

	}
	
	@ExceptionHandler(value = { ActionNotPermittedException.class })
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(ActionNotPermittedException ex) {
		log.error("Access not allowed - ", ex.getCause());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.FORBIDDEN.getReasonPhrase());
		response.setStatusCode(HttpStatus.FORBIDDEN.value());
		response.setError(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(value = { InvalidAccountNumberException.class })
	public ResponseEntity<ErrorResponse> handleInvalidAccountNumberException(InvalidAccountNumberException ex) {
		log.error("Invalid account input - ", ex.getCause());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.NOT_FOUND.getReasonPhrase());
		response.setStatusCode(HttpStatus.NOT_FOUND.value());
		response.setError(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = { MethodArgumentNotValidException.class })
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error("User input error : ", ex.getMessage());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError("Invalid input for field - " + ex.getBindingResult().getFieldErrors().get(0).getField());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class })
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		log.error("User input error : ", ex.getMessage());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError("Invalid input for field - " + ex.getName());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = { MissingServletRequestParameterException.class })
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
		log.error("User input error : ", ex.getMessage());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.BAD_REQUEST.getReasonPhrase());
		response.setStatusCode(HttpStatus.BAD_REQUEST.value());
		response.setError("Invalid input for field - " + ex.getParameterName());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = { Exception.class })
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {
		log.error("Unexpected Exception: ", ex.getMessage());
		ErrorResponse response = new ErrorResponse();
		response.setTimestamp(LocalDateTime.now());
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
		response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setError(ex.getMessage());
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
