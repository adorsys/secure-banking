package org.adorsys.encobject.service;

/**
 * Requested container not found
 * 
 * @author fpo
 *
 */
public class UnknownContainerException extends Exception {
	private static final long serialVersionUID = 2434653140109032234L;

	public UnknownContainerException(String container) {
		super(container);
	}
}
