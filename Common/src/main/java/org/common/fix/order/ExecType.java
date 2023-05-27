package org.common.fix.order;

public enum ExecType {
    NEW('0'),
    DONE_FOR_DAY('3'),
    CANCELED('4'),
    REPLACED('5'),
    PENDING_CANCEL('6'),
    STOPPED('7'),
    REJECTED('8'),
    SUSPENDED('9'),
    PENDING_NEW('A'),
    CALCULATED('B'),
    EXPIRED('C'),
    RESTATED('D'),
    PENDING_REPLACE('E'),
    TRADE('F'),
    TRADE_CORRECT('G'),
    TRADE_CANCEL('H'),
    ORDER_STATUS('I');

    public final char label;

    ExecType(char label) {
        this.label = label;
    }

    public static ExecType fromValue(char value) {
        for (ExecType execType : ExecType.values()) {
            if (execType.label == value) {
                return execType;
            }
        }
        throw new IllegalArgumentException("Unknown ExecType value: " + value);
    }

    @Override
    public String toString() {
        return Character.toString(label);
    }
}