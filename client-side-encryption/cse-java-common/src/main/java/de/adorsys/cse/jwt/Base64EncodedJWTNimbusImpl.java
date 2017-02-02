package de.adorsys.cse.jwt;

import java.text.ParseException;

public class Base64EncodedJWTNimbusImpl implements Base64EncodedJWT {
    private String container;

    public Base64EncodedJWTNimbusImpl(String base64String) {
//        PlainJWT.parse(base64String)

        this.container = base64String;
    }

    @Override
    public JWT decode() throws ParseException {
        return new JWTNimbusImpl(container);
    }
}
