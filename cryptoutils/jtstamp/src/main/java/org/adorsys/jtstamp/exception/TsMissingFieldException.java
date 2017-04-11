package org.adorsys.jtstamp.exception;

/**
 * Indicates a missing field in the input data.
 * 
 * The message is the name of the field missing.
 * 
 * @author fpo
 *
 */
public class TsMissingFieldException extends Exception {

	private static final long serialVersionUID = 5429591881427696971L;
	public TsMissingFieldException(String message) {
		super(message);
	}
}
