package de.adorsys.android.securedevicestorage;

public class CryptoException extends RuntimeException {
    public CryptoException(String detailMessage, Throwable cause) {
        super(detailMessage, cause);
    }
}
