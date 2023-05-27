package org.common.fix.login;

public enum EncryptMethod {
    NONE_OTHER(0),
    PKCS(1),
    DES_ECB(2),
    PKCS_DES(3),
    PGP_DES_DEFUNCT(4),
    PGP_DES_MD5(5),
    PEM_DES_MD5(6);

    private final int value;

    EncryptMethod(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns the encryption method by its integer value.
     *
     * @param value The integer value of the encryption method
     * @return The encryption method
     */
    public static EncryptMethod fromValue(int value) {
        for (EncryptMethod method : values()) {
            if (method.getValue() == value) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid EncryptMethod value: " + value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
