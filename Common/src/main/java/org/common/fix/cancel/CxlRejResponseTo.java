package org.common.fix.cancel;

public enum CxlRejResponseTo {
    ORDER_CANCEL_REQUEST('1'),
    ORDER_CANCEL_REPLACE_REQUEST('2');

    private final char value;

    CxlRejResponseTo(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static CxlRejResponseTo fromString(String s) {
        char value = s.charAt(0);
        for (CxlRejResponseTo responseTo : values()) {
            if (responseTo.getValue() == value) {
                return responseTo;
            }
        }
        throw new IllegalArgumentException("Invalid CxlRejResponseTo value: " + s);
    }
}