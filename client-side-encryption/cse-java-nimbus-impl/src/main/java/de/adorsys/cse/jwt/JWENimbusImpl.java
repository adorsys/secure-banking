package de.adorsys.cse.jwt;

import com.nimbusds.jose.JWEObject;

public class JWENimbusImpl implements JWE {
    private JWEObject container;

    JWENimbusImpl(JWEObject container) {
        this.container = container;
    }

    @Override
    public String encode() {
        return container.serialize();
    }
}
