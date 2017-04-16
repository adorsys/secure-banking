package org.adorsys.encobject.service;

/**
 * Object with corresponding handle not found in the object storage.
 * 
 * @author fpo
 *
 */
public class ObjectNotFoundException extends Exception {

	private static final long serialVersionUID = -5566918389843503687L;

	public ObjectNotFoundException() {
	}

	public ObjectNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectNotFoundException(String message) {
		super(message);
	}

	public ObjectNotFoundException(Throwable cause) {
		super(cause);
	}
}
