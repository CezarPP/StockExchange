package org.exchange.ports;

import org.common.fix.FixMessage;
import org.common.fix.FixTrailer;
import org.common.fix.body.*;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.market_data.MarketDataEntry;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.symbols.Symbol;
import org.exchange.book.*;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A fix engine port for each client
 */
public class FixEnginePort {
    public final String exchangeCompId = "Exchange SRL";
    public int crtSeqNr = 0;
    private final BufferedReader in;
    private final PrintWriter out;

    private String clientCompId = null;

    public FixEnginePort(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void setClientCompId(String clientCompId) {
        this.clientCompId = clientCompId;
    }

    public void sendMarketData(FixMessage fixMessage) {
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
        sendBody(fixBodyMarketData, MessageType.MarketDataSnapshotFullRefresh);
    }

    public void sendLogin(FixMessage fixMessage) {
        FixBodyLogin fixBodyLoginReceived = FixBodyLogin.fromString(fixMessage.body().toString());

        crtSeqNr++;

        FixBodyLogin fixBody = new FixBodyLogin(fixBodyLoginReceived.encryptMethod, fixBodyLoginReceived.heartBeat, fixBodyLoginReceived.username);
        FixHeader fixHeader =
                new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.Logon,
                        exchangeCompId, fixMessage.header().senderCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    public void sendLogout(FixMessage fixMessage) {
        crtSeqNr++;

        FixBodyLogout fixBody = new FixBodyLogout("Logout acknowledgment");
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.Logout,
                exchangeCompId, fixMessage.header().senderCompID, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    private void sendBody(FixBody fixBody, MessageType messageType) {
        ++crtSeqNr;
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(),
                messageType, exchangeCompId,
                clientCompId, crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    public void send(FixMessage fixMessage) {
        System.out.println("Port is sending fix message " + fixMessage);
        out.println(fixMessage);
        out.flush();
    }
}
