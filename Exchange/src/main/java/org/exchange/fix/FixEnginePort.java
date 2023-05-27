package org.exchange.fix;

import org.common.fix.FixMessage;
import org.common.fix.FixTrailer;
import org.common.fix.body.FixBodyMarketData;
import org.common.fix.body.FixBodyRequest;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.market_data.MarketDataEntry;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.symbols.Symbol;
import org.exchange.book.Limit;
import org.exchange.book.OrderBook;
import org.exchange.book.OrderBookFactory;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class FixEnginePort {
    int crtSeqNr = 0;
    int orderID = 0;
    private final BufferedReader in;
    private final PrintWriter out;

    public FixEnginePort(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

/*    private void sendLogin() {
        crtSeqNr++;

        FixBodyLogin fixBody = new FixBodyLogin(EncryptMethod.NONE_OTHER, 1000, username);
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.Logon,
                senderCompID, targetCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }*/

    public void sendMarketData(FixMessage fixMessage) {
        System.out.println("Sending market data...");
        crtSeqNr++;

        FixBodyRequest fixBodyRequest = FixBodyRequest.fromString(fixMessage.body().toString());
        Symbol symbol = Symbol.fromValue(fixBodyRequest.symbols.get(0));
        OrderBook orderBook = OrderBookFactory.getOrderBook(symbol);
        List<MarketDataEntry> marketDataEntryList = new ArrayList<>();

        for (MarketDataEntryType marketDataEntryType : fixBodyRequest.mdEntryTypes) {
            List<Limit> limitList;
            if (marketDataEntryType == MarketDataEntryType.BID) {
                limitList = orderBook.getFirstBuyEntries(fixBodyRequest.marketDepth);
            } else {
                assert (marketDataEntryType == MarketDataEntryType.OFFER);
                limitList = orderBook.getFirstSellEntries(fixBodyRequest.marketDepth);
            }
            for (int i = 0; i < limitList.size(); i++) {
                Limit limit = limitList.get(i);
                marketDataEntryList.add(limit.getMarketDataEntry(i));
            }
        }

        FixBodyMarketData fixBodyMarketData = new FixBodyMarketData(symbol.getSymbol(),
                marketDataEntryList.size(), marketDataEntryList);
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBodyMarketData.toString().length(),
                MessageType.MarketDataSnapshotFullRefresh, "Cezar SRL",
                "Exchange SRL", crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBodyMarketData, FixTrailer.getTrailer(fixHeader, fixBodyMarketData)));
    }

    private void send(FixMessage fixMessage) {
        System.out.println("Port is sending fix message " + fixMessage);
        out.println(fixMessage);
        out.flush();
    }
}
