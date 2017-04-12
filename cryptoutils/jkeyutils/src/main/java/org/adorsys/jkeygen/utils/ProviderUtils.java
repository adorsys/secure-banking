package org.adorsys.jkeygen.utils;

import java.security.Provider;
import java.security.Security;

import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ProviderUtils {

	public static final Provider bcProvider;

	static {
		Security.addProvider(new BouncyCastleProvider());	
		bcProvider = Security.getProvider("BC");
		if(bcProvider==null) throw new IllegalStateException( new NoSuchPaddingException("BC"));
	}
}
