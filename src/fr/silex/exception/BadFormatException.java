package fr.silex.exception;

public class BadFormatException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadFormatException(String message) {
		super(message);
	}
	
}
