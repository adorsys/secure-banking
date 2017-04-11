package org.adorsys.envutils;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class EnvPropPasswordCallbackHandler implements CallbackHandler {
	
	private final String passwordEnvProperty;

	public EnvPropPasswordCallbackHandler(String passwordEnvProperty) {
		this.passwordEnvProperty = passwordEnvProperty;
	}

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		if(callbacks==null || callbacks.length==0) throw new IOException("No callback provided");
		
		if(!(callbacks[0] instanceof PasswordCallback)) throw new UnsupportedCallbackException(callbacks[0]);
		
		((PasswordCallback)callbacks[0]).setPassword(EnvProperties.getEnvOrSysProp(passwordEnvProperty, false).toCharArray());
	}

}
