package org.adorsys.encobject.service;

/**
 * Object with corresponding handle not found in the object storage.
 * 
 * @author fpo
 *
 */
public class ObjectInfoNotFoundException extends Exception {

	private static final long serialVersionUID = -5566918389843503687L;

	public ObjectInfoNotFoundException() {
	}

	public ObjectInfoNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ObjectInfoNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ObjectInfoNotFoundException(String message) {
		super(message);
	}

	public ObjectInfoNotFoundException(Throwable cause) {
		super(cause);
	}
}
