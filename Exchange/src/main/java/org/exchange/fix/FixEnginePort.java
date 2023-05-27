package org.exchange.fix;

import org.common.fix.FixMessage;
import org.common.fix.FixTrailer;
import org.common.fix.body.FixBodyExecutionReport;
import org.common.fix.body.FixBodyMarketData;
import org.common.fix.body.FixBodyOrder;
import org.common.fix.body.FixBodyRequest;
import org.common.fix.header.BeginString;
import org.common.fix.header.FixHeader;
import org.common.fix.header.MessageType;
import org.common.fix.market_data.MarketDataEntry;
import org.common.fix.market_data.MarketDataEntryType;
import org.common.fix.order.ExecType;
import org.common.fix.order.OrderStatus;
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
    int crtSeqNr = 0;
    private final BufferedReader in;
    private final PrintWriter out;

    public FixEnginePort(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }

    public void sendMarketData(FixMessage fixMessage) {
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

    public void sendOrderReceiveConfirmation(FixBodyOrder fixBodyOrder, Order order, int execId) {
        FixBodyExecutionReport fixBodyExecutionReport =
                new FixBodyExecutionReport(Integer.toString(order.getId()), fixBodyOrder.clientOrderID,
                        Integer.toString(execId), ExecType.NEW, OrderStatus.NEW, order.getSymbol(),
                        order.getSide(), order.getPrice(), order.getQuantity(), 0, 0);
        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBodyExecutionReport.toString().length(),
                MessageType.ExecutionReport, "Exchange SRL", "Cezar SRL", crtSeqNr, OffsetDateTime.now());
        send(new FixMessage(fixHeader, fixBodyExecutionReport, FixTrailer.getTrailer(fixHeader, fixBodyExecutionReport)));
    }

    private void send(FixMessage fixMessage) {
        System.out.println("Port is sending fix message " + fixMessage);
        out.println(fixMessage);
        out.flush();
    }
}
