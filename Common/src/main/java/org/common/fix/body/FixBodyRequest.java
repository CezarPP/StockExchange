package org.common.fix.body;

import org.common.fix.FixMessage;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.fix.market_data.SubscriptionRequestType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixBodyRequest implements FixBody {
    /**
     * 262 -> MDReqID -> market data request id ->
     * Must be unique, or the ID of previous Market Data Request to disable in the case of subscription
     */
    public int marketDataRequestID;

    /**
     * 263 -> SubscriptionRequestType
     */
    public SubscriptionRequestType subscriptionRequestType;

    /**
     * 264 -> MarketDepth -> number of bid/ask orders of the top of the book, 0 for full book
     */
    public int marketDepth;

    /**
     * 267 -> NoMDEntryTypes -> number of MDEntryType <269> fields requested
     */
    public int noMdEntryTypes;

    /**
     * 269 -> MDEntryType -> a list of all the types of Market Data Entries that the client is interested in
     */
    public List<MarketDataEntryType> mdEntryTypes;

    /**
     * 146 -> NoRelatedSym -> number of symbols requested.
     */
    public int noRelatedSym;
    /**
     * 55 -> Symbol
     */
    public List<String> symbols;

    public FixBodyRequest(int marketDataRequestID, SubscriptionRequestType subscriptionRequestType,
                          int marketDepth, List<MarketDataEntryType> mdEntryTypes, List<String> symbols) {
        this.marketDataRequestID = marketDataRequestID;
        this.subscriptionRequestType = subscriptionRequestType;
        this.marketDepth = marketDepth;
        this.noMdEntryTypes = mdEntryTypes.size();
        this.mdEntryTypes = mdEntryTypes;
        this.noRelatedSym = symbols.size();
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        StringBuilder ans = new StringBuilder("262=" + marketDataRequestID + FixMessage.delimiter +
                "263=" + subscriptionRequestType.getValue() + FixMessage.delimiter +
                "264=" + marketDepth + FixMessage.delimiter +
                "267=" + noMdEntryTypes + FixMessage.delimiter);
        for (MarketDataEntryType marketDataEntryType : mdEntryTypes) {
            ans.append("269=").append(marketDataEntryType.getValue()).append(FixMessage.delimiter);
        }
        ans.append("146=").append(noRelatedSym).append(FixMessage.delimiter);
        for (String symbol : symbols)
            ans.append("55=").append(symbol).append(FixMessage.delimiter);
        return ans.toString();
    }

    public static FixBodyRequest fromString(String str) {
        Map<String, String> map = new HashMap<>();
        List<MarketDataEntryType> mdEntryTypes = new ArrayList<>();
        List<String> symbols = new ArrayList<>();

        String[] tokens = str.split(FixMessage.delimiter);
        for (String token : tokens) {
            String[] keyValue = token.split("=");
            if (keyValue[0].equals("269")) {
                mdEntryTypes.add(MarketDataEntryType.fromValue(keyValue[1].charAt(0)));
            } else if (keyValue[0].equals("55")) {
                symbols.add(keyValue[1]);
            } else {
                map.put(keyValue[0], keyValue[1]);
            }
        }

        return new FixBodyRequest(
                Integer.parseInt(map.get("262")),
                SubscriptionRequestType.fromValue(map.get("263").charAt(0)),
                Integer.parseInt(map.get("264")),
                mdEntryTypes,
                symbols
        );
    }

}
