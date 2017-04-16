package org.adorsys.encobject.params;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;

public class EncryptionParams {
	private JWEAlgorithm encAlgo;
	private EncryptionMethod encMethod;

	public JWEAlgorithm getEncAlgo() {
		return encAlgo;
	}

	public void setEncAlgo(JWEAlgorithm encAlgo) {
		this.encAlgo = encAlgo;
	}

	public EncryptionMethod getEncMethod() {
		return encMethod;
	}

	public void setEncMethod(EncryptionMethod encMethod) {
		this.encMethod = encMethod;
	}

	public static class Builder {
		EncryptionParams ep = new EncryptionParams();

		public Builder setEncAlgo(JWEAlgorithm encAlgo) {
			ep.setEncAlgo(encAlgo);
			return this;
		}

		public Builder setEncMethod(EncryptionMethod encMethod) {
			ep.setEncMethod(encMethod);
			return this;
		}

		public EncryptionParams build() {
			return ep;
		}
	}
}
