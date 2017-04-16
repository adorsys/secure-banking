package org.adorsys.jjwk.selector;

public class UnsupportedKeyLengthException extends Exception {
	private static final long serialVersionUID = -102550810645375099L;

	public UnsupportedKeyLengthException(String message) {
		super(message);
	}

	public UnsupportedKeyLengthException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
