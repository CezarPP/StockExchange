package org.common.fix.market_data;

public enum MarketDataEntryType {
    BID('0'),
    OFFER('1'),
    TRADE('2'),
    INDEX_VALUE('3'),
    OPENING_PRICE('4'),
    CLOSING_PRICE('5'),
    SETTLEMENT_PRICE('6'),
    TRADING_SESSION_HIGH_PRICE('7'),
    TRADING_SESSION_LOW_PRICE('8'),
    TRADING_SESSION_VWAP_PRICE('9'),
    IMBALANCE('A'),
    TRADE_VOLUME('B'),
    OPEN_INTEREST('C');

    private final char value;

    MarketDataEntryType(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    /**
     * @param value The char value of the market data entry type
     * @return The market data entry type
     */
    public static MarketDataEntryType fromValue(char value) {
        for (MarketDataEntryType entryType : values()) {
            if (entryType.getValue() == value) {
                return entryType;
            }
        }
        throw new IllegalArgumentException("Invalid MarketDataEntryType value: " + value);
    }

    @Override
    public String toString() {
        return Character.toString(value);
    }
}
