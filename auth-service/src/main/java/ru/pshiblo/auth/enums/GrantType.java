package ru.pshiblo.auth.enums;

public enum GrantType {
    REFRESH_TOKEN,
    CLIENT_CREDITIONALS,
    PASSWORD,
    AUTHORIZATION_CODE;

    public static GrantType toGrantType(String grantType) {
        for (GrantType value : values()) {
            if (value.name().equalsIgnoreCase(grantType)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Grant not exist");
    }
}
