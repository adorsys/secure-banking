package de.adorsys.cse.example.util;

import de.adorsys.cse.nonce.NonceGenerator;

import java.util.UUID;

public class UUIDNonceGenerator implements NonceGenerator {
    @Override
    public String generateNonce() {
        return UUID.randomUUID().toString();
    }
}
