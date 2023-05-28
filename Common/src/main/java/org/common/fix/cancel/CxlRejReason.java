package org.common.fix.cancel;

public enum CxlRejReason {
    TOO_LATE_TO_CANCEL(0),
    UNKNOWN_ORDER(1),
    BROKER_OPTION(2),
    ORDER_ALREADY_IN_PENDING_STATUS(3);

    private final int value;

    CxlRejReason(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value);
    }

    public static CxlRejReason fromString(String s) {
        int value = Integer.parseInt(s);
        for (CxlRejReason reason : values()) {
            if (reason.getValue() == value) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Invalid CxlRejReason value: " + s);
    }
}
