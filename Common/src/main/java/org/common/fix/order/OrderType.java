package org.common.fix.order;

public enum OrderType {
    MARKET('1'),
    LIMIT('2'),
    STOP('3'),
    STOP_LIMIT('4'),
    WITH_OR_WITHOUT('6'),
    LIMIT_OR_BETTER('7'),
    LIMIT_WITH_OR_WITHOUT('8'),
    ON_BASIS('9'),
    PREVIOUSLY_QUOTED('D'),
    PREVIOUSLY_INDICATED('E'),
    FOREX_SWAP('G'),
    FUNARI('I'),
    MARKET_IF_TOUCHED('J'),
    MARKET_WITH_LEFTOVER_AS_LIMIT('K'),
    PREVIOUS_FUND_VALUATION_POINT('L'),
    NEXT_FUND_VALUATION_POINT('M'),
    PEGGED('P');

    final public char label;

    OrderType(char label) {
        this.label = label;
    }

    public static OrderType fromValue(char value) {
        for (OrderType orderType : OrderType.values()) {
            if (orderType.label == value) {
                return orderType;
            }
        }
        throw new IllegalArgumentException("Unknown OrderType value: " + value);
    }

    @Override
    public String toString() {
        return Character.toString(label);
    }
}