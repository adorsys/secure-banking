package org.adorsys.jkeygen.keystore;

import javax.security.auth.callback.CallbackHandler;

public class KeyEntryData {

	private final CallbackHandler passwordSource;
	
	private final String alias;

	public KeyEntryData(String alias, CallbackHandler passwordSource) {
		this.alias = alias;
		this.passwordSource = passwordSource;
	}

	public CallbackHandler getPasswordSource() {
		return passwordSource;
	}

	public String getAlias() {
		return alias;
	}
}
