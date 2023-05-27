package org.common.fix.order;

public enum PriceType {
    PERCENTAGE(1),
    PER_UNIT(2),
    FIXED_AMOUNT(3),
    DISCOUNT(4),
    PREMIUM(5),
    SPREAD(6),
    TED_PRICE(7),
    TED_YIELD(8),
    YIELD(9),
    FIXED_CABINET_TRADE_PRICE(10),
    VARIABLE_CABINET_TRADE_PRICE(11);

    private final int value;

    PriceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PriceType fromValue(int value) {
        for (PriceType priceType : PriceType.values()) {
            if (priceType.getValue() == value) {
                return priceType;
            }
        }
        throw new IllegalArgumentException("Unknown PriceType value: " + value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}