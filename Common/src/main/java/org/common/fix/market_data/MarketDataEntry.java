package org.common.fix.market_data;

import org.common.fix.FixMessage;

public class MarketDataEntry {
    /**
     * 269 -> MDEntryType -> First field in the repeating group
     */
    private final MarketDataEntryType marketDataEntryType;

    /**
     * Conditionally required if MDEntryType <269> is not 'A'(Imbalance), 'B'(Trade Volume), or 'C'(Open Interest)
     * 270 -> MDEntryPx -> price
     */
    private final float price;

    /**
     * Quantity or volume represented by the Market Data Entry.
     * 271 -> MDEntrySize -> Required if MDEntryType is Bid/Offer
     */
    private final int quantity;

    /**
     * In an Aggregated Book, used to show how many individual orders make up an MDEntry
     * 346 -> NumberOfOrders
     */
    private final int nrOrders;

    /**
     * Display position of a bid or offer, numbered from most competitive to least competitive, per market side, beginning with 1
     * 290 -> MDEntryPositionNo
     */
    private final int mdEntryPositionNo;

    public MarketDataEntry(MarketDataEntryType marketDataEntryType, float price, int quantity, int nrOrders, int mdEntryPositionNo) {
        this.marketDataEntryType = marketDataEntryType;
        this.price = price;
        this.quantity = quantity;
        this.nrOrders = nrOrders;
        this.mdEntryPositionNo = mdEntryPositionNo;
    }

    private MarketDataEntry(Builder builder) {
        this.marketDataEntryType = builder.marketDataEntryType;
        this.price = builder.price;
        this.quantity = builder.mdEntrySize;
        this.nrOrders = builder.nrOrders;
        this.mdEntryPositionNo = builder.mdEntryPositionNo;
    }

    @Override
    public String toString() {
        return "269=" + marketDataEntryType.getValue() + FixMessage.delimiter +
                "270=" + price + FixMessage.delimiter +
                "271=" + quantity + FixMessage.delimiter +
                "346=" + nrOrders + FixMessage.delimiter +
                "290=" + mdEntryPositionNo + FixMessage.delimiter;
    }

    public static MarketDataEntry fromString(String str) {
        String[] parts = str.split(FixMessage.delimiter);

        Builder builder = new Builder();
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("Malformed string");
            }
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "269" -> builder.withMarketDataEntryType(MarketDataEntryType.fromValue(value.charAt(0)));
                case "270" -> builder.withPrice(Float.parseFloat(value));
                case "271" -> builder.withMdEntrySize(Integer.parseInt(value));
                case "346" -> builder.withNrOrders(Integer.parseInt(value));
                case "290" -> builder.withMdEntryPositionNo(Integer.parseInt(value));
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            }
        }

        return builder.build();
    }

    public static class Builder {
        private MarketDataEntryType marketDataEntryType;
        private float price;
        private int mdEntrySize;
        private int nrOrders;
        private int mdEntryPositionNo;

        public Builder withMarketDataEntryType(MarketDataEntryType marketDataEntryType) {
            this.marketDataEntryType = marketDataEntryType;
            return this;
        }

        public Builder withPrice(float price) {
            this.price = price;
            return this;
        }

        public Builder withMdEntrySize(int mdEntrySize) {
            this.mdEntrySize = mdEntrySize;
            return this;
        }

        public Builder withNrOrders(int nrOrders) {
            this.nrOrders = nrOrders;
            return this;
        }

        public Builder withMdEntryPositionNo(int mdEntryPositionNo) {
            this.mdEntryPositionNo = mdEntryPositionNo;
            return this;
        }

        public MarketDataEntry build() {
            return new MarketDataEntry(this);
        }
    }

    public MarketDataEntryType getMarketDataEntryType() {
        return marketDataEntryType;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getNrOrders() {
        return nrOrders;
    }

    public int getMdEntryPositionNo() {
        return mdEntryPositionNo;
    }
}
