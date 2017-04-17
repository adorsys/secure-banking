package org.adorsys.encobject.service;

/**
 * Thrown if framework tries to create an existing container.
 * 
 * @author fpo
 *
 */
public class ContainerExistsException extends Exception {
	private static final long serialVersionUID = -8578789707987332675L;

	public ContainerExistsException(String container) {
		super(container);
	}
}
