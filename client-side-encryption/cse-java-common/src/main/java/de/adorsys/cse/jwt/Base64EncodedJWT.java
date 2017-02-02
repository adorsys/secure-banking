package de.adorsys.cse.jwt;

import java.text.ParseException;

public interface Base64EncodedJWT {
    JWT decode() throws ParseException;
}
