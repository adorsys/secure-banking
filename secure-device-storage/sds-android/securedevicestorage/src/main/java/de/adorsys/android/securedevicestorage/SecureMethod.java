package de.adorsys.android.securedevicestorage;

public enum SecureMethod {
    METHOD_ENCRYPT("methodEncrypt"), METHOD_HASH("methodHash");

    private final String method;
    SecureMethod(final String text) {
        this.method = text;
    }

    @Override
    public String toString() {
        return method;
    }
}