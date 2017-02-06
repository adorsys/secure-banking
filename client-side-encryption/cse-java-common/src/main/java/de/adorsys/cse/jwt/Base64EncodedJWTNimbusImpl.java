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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Base64EncodedJWTNimbusImpl that = (Base64EncodedJWTNimbusImpl) o;

        return container.equals(that.container);
    }

    @Override
    public int hashCode() {
        return container.hashCode();
    }
}
