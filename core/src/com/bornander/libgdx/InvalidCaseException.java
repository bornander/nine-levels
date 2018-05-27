package com.bornander.libgdx;

public class InvalidCaseException extends RuntimeException {

	private static final long serialVersionUID = 1777075509829991738L;
	
	private final Object value;
	
	public InvalidCaseException(Object value) {
		this.value = value;
	}
	
	@Override
	public String getMessage() {
		return String.format("Invalid case, '%s', cannot be handled", value);
	}
}