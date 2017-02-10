package de.adorsys.cse;

import java.util.Random;

public class Base64StringGenerator {
    private static final String base64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    public static String generateRandomBase64String(int length)
    {
        Random rng = new Random(System.currentTimeMillis());
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = base64Chars.charAt(rng.nextInt(base64Chars.length()));
        }
        return new String(text);
    }

}
