package de.adorsys.cse.example.client.crypto.bean;

import java.util.HashMap;
import java.util.Map;

public class JWEDecryptedResponse {
    private Map<String, Object> decryptedSecrets = new HashMap<>();

    public Map<String, Object> getDecryptedSecrets() {
        return decryptedSecrets;
    }

    public void setDecryptedSecrets(Map<String, Object> decryptedSecrets) {
        this.decryptedSecrets = decryptedSecrets;
    }
}
