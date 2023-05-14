package org.common.fix.order;

public enum OrderStatus {
    NEW('0'),
    PARTIALLY_FILLED('1'),
    FILLED('2'),
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
    ACCEPTED_FOR_BIDDING('D'),
    PENDING_REPLACE('E');

    final public char label;

    OrderStatus(char label) {
        this.label = label;
    }

    public static OrderStatus fromValue(char value) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.label == value) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Unknown OrderStatus value: " + value);
    }
}
