package org.adorsys.jkeygen.pwd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public final class PasswordMapCallbackHandler implements CallbackHandler {
	
	private Map<String, char[]> passwordMap = new HashMap<>();

	private PasswordMapCallbackHandler() {
	}

	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
		if (!(callbacks[0] instanceof PasswordCallback)) {
			throw new UnsupportedCallbackException(callbacks[0]);
		} else {
			PasswordCallback passwordCallback = (PasswordCallback) callbacks[0];
			char[] cs = passwordMap.get(passwordCallback.getPrompt());
			passwordCallback.setPassword(cs==null?null:(char[]) cs.clone());
		}
	}

	protected void finalize() throws Throwable {
		Collection<char[]> values = passwordMap.values();
		for (char[] password : values) {
			if (password != null) {
				Arrays.fill(password, ' ');
			}
		}
		super.finalize();
	}
	
	public static class Builder {
		PasswordMapCallbackHandler handler = new PasswordMapCallbackHandler();
		public Builder withEntry(String alias, char[] password){
			if (password != null) {
				char[] pwd = (char[]) password.clone();
				handler.passwordMap.put(alias, pwd);
			}
			return this;
		}
		
		public PasswordMapCallbackHandler build(){
			return handler;
		}
	}

}
