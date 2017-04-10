package org.adorsys.jjwk.selector;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;

public class JWSSignerAndAlgorithm {
	
    private final JWSSigner signer;
    private final JWSAlgorithm jwsAlgorithm;

	public JWSSignerAndAlgorithm(JWSSigner signer, JWSAlgorithm jwsAlgorithm) {
		this.signer = signer;
		this.jwsAlgorithm = jwsAlgorithm;
	}
	public JWSSigner getSigner() {
		return signer;
	}
	public JWSAlgorithm getJwsAlgorithm() {
		return jwsAlgorithm;
	}
}
