package org.common.fix;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class FixEngine {
    final String clientOrderID = "Cezar";
    static int crtSeqNr = 0;

    // TODO(member variables)
    FixEngine() {
        // TODO(Connecting to exchange)
    }

    void sendNewSingleOrderLimit(String symbol, Side side, int quantity, int price) {
        crtSeqNr++;

        FixBody fixBody = new FixBody();
        fixBody.clientOrderID = this.clientOrderID + crtSeqNr;
        fixBody.symbol = symbol;
        fixBody.side = side;
        fixBody.transactTime = OffsetDateTime.now(ZoneOffset.UTC);
        fixBody.orderQuantity = quantity;
        fixBody.orderType = OrderType.Limit;
        fixBody.price = price;

        FixHeader fixHeader = new FixHeader(BeginString.Fix_4_4, fixBody.toString().length(), MessageType.NewOrderSingle,
                "Cezar SRL", "Exchange SRL", crtSeqNr, OffsetDateTime.now());

        send(new FixMessage(fixHeader, fixBody, FixTrailer.getTrailer(fixHeader, fixBody)));
    }

    private void send(FixMessage fixMessage) {
        // TODO()
    }

}
