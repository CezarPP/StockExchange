package org.common.fix.market_data;

public enum SubscriptionRequestType {
    SNAPSHOT('0'),
    SNAPSHOT_AND_UPDATES_SUBSCRIBE('1'),
    DISABLE_PREV_SNAPSHOT_AND_UPDATES_UNSUBSCRIBE('2');

    private final char value;

    SubscriptionRequestType(char value) {
        this.value = value;
    }

    public char getValue() {
        return value;
    }

    /**
     * @param value The character value of the subscription request type
     * @return The subscription request type
     */
    public static SubscriptionRequestType fromValue(char value) {
        for (SubscriptionRequestType requestType : values()) {
            if (requestType.getValue() == value) {
                return requestType;
            }
        }
        throw new IllegalArgumentException("Invalid SubscriptionRequestType value: " + value);
    }
}
