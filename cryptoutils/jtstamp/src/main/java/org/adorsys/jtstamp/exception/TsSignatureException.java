package org.adorsys.jtstamp.exception;

/**
 * Indicates that the claim could not be signed.
 * 
 * @author fpo
 *
 */
public class TsSignatureException extends Exception {

	private static final long serialVersionUID = 2592684884398395683L;

	public TsSignatureException(Throwable cause) {
		super(cause);
	}
}
