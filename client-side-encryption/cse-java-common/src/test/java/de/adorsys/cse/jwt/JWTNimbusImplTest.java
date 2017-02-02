package de.adorsys.cse.jwt;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JWTNimbusImplTest {
    private JWT jwt;

    @Test
    public void serializationReturnsSameString() throws Exception {
        final String base64encodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";

        jwt = new JWTNimbusImpl(base64encodedToken);
        assertEquals("Serializaion/Deserializaion returns same string", base64encodedToken, jwt.toString());
    }

    @Test
    public void serializationReturnsBase64EncodedJWT() throws Exception {
        final String base64encodedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ";
        Base64EncodedJWT expectedEncodedJWT = new Base64EncodedJWTNimbusImpl(base64encodedToken);

        jwt = new JWTNimbusImpl(base64encodedToken);
        Base64EncodedJWT encodedJWT = jwt.encode();
        assertEquals("Serializaion/Deserializaion returns same string", expectedEncodedJWT, jwt.encode());
    }

}
