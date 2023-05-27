package org.common.fix.order;

public enum Side {
    BUY('1'),
    SELL('2');

    public final char label;

    Side(char label) {
        this.label = label;
    }

    public static Side fromValue(char value) {
        for (Side side : Side.values()) {
            if (side.label == value) {
                return side;
            }
        }
        throw new IllegalArgumentException("Unknown Side value: " + value);
    }

    @Override
    public String toString() {
        return Character.toString(label);
    }
}