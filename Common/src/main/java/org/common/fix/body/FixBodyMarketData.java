package org.common.fix.body;

import org.common.fix.FixMessage;
import org.common.fix.market_data.MarketDataEntry;

import java.util.ArrayList;
import java.util.List;

public class FixBodyMarketData implements FixBody {
    /**
     * 55 -> Symbol
     */
    public final String symbol;
    /**
     * 268 -> NoMDEntries -> Number of entries following
     */
    public final int noMDEntries;

    public final List<MarketDataEntry> marketDataEntries;

    public FixBodyMarketData(String symbol, int noMDEntries, List<MarketDataEntry> marketDataEntries) {
        this.symbol = symbol;
        this.noMDEntries = noMDEntries;
        this.marketDataEntries = marketDataEntries;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder("55=" + symbol + FixMessage.delimiter +
                "268=" + noMDEntries + FixMessage.delimiter);
        for (MarketDataEntry marketDataEntry : marketDataEntries)
            ans.append(marketDataEntry.toString());
        return ans.toString();
    }

    public static FixBodyMarketData fromString(String str) {
        String[] parts = str.split(FixMessage.delimiter);

        String symbol = null;
        int noMDEntries = 0;
        List<MarketDataEntry> marketDataEntries = new ArrayList<>();

        int i = 0;
        while (i < parts.length && i < 2) {
            String[] keyValue = parts[i].split("=");
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "55" -> symbol = value;
                case "268" -> noMDEntries = Integer.parseInt(value);
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            }

            i++;
        }

        // Assuming that the remaining parts are MarketDataEntryAggregated entries
        while (i < parts.length) {
            StringBuilder marketDataEntry = new StringBuilder();
            int j;
            for (j = 0; j < 5 && i < parts.length; j++, i++)
                marketDataEntry.append(parts[i]);
            if (j != 5)
                throw new IllegalArgumentException("Malformed MarketDataEntryAggregated");
            marketDataEntries.add(MarketDataEntry.fromString(marketDataEntry.toString()));
        }

        if (marketDataEntries.size() != noMDEntries) {
            throw new IllegalArgumentException("Mismatch between declared number of entries (" + noMDEntries + ") and actual (" + marketDataEntries.size() + ")");
        }

        return new FixBodyMarketData(symbol, noMDEntries, marketDataEntries);
    }

}

