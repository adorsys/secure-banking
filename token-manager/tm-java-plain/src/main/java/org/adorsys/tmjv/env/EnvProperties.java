package org.adorsys.tmjv.env;

public class EnvProperties {

    public static String getEnvProp(String propName, boolean optional) {
        String propValue = System.getenv(propName);

        if (propValue == null || propValue.trim().length() == 0) {
            if (optional) {
                return null;
            }
            throw new IllegalStateException("Missing environment property " + propName);
        }
        return propValue;
    }

    public static String getEnvProp(String propName, String defaultValue) {
        String propValue = System.getenv(propName);

        if (propValue == null || propValue.trim().length() == 0) {
            return defaultValue;
        }
        return propValue;
    }

}
