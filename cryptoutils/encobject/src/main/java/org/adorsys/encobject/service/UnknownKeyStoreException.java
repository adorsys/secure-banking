package org.adorsys.encobject.service;

/**
 * Key store with the specified id is not known to the realm.
 * 
 * @author fpo
 *
 */
public class UnknownKeyStoreException extends Exception {
	private static final long serialVersionUID = 8991181017868945135L;
	public UnknownKeyStoreException(String message) {
		super(message);
	}
}
