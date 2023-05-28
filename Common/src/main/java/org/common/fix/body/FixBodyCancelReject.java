package org.common.fix.body;

import org.common.fix.FixMessage;
import org.common.fix.cancel.CxlRejReason;
import org.common.fix.cancel.CxlRejResponseTo;
import org.common.fix.order.OrderStatus;

import java.util.HashMap;
import java.util.Map;

public class FixBodyCancelReject implements FixBody {
    /**
     * 37 -> OrderID -> If CxlRejReason <102>='Unknown order', specify 'NONE'.
     */
    String orderId;

    /**
     * 11 -> ClOrdID -> Unique order id assigned by institution to the cancel request or to the replacement order.
     */
    String clOrderId;

    /**
     * 41 -> OrigClOrdID -> ClOrdID <11> which could not be canceled/replaced.
     */
    String origClientOrderId;

    /**
     * 39 -> OrdStatus -> OrdStatus <39> value after this cancel reject is applied.
     */
    OrderStatus orderStatus;

    /**
     * 434 -> CxlRejResponseTo -> Either Order Cancel Request <F> or Order Cancel/Replace Request <G>
     */
    CxlRejResponseTo cxlRejResponseTo;

    /**
     * 102 -> CxlRejReason -> Reason for rejecting the order
     */
    CxlRejReason cxlRejReason;

    public FixBodyCancelReject(String orderId, String clOrderId, String origClientOrderId, OrderStatus orderStatus, CxlRejResponseTo cxlRejResponseTo, CxlRejReason cxlRejReason) {
        this.orderId = orderId;
        this.clOrderId = clOrderId;
        this.origClientOrderId = origClientOrderId;
        this.orderStatus = orderStatus;
        this.cxlRejResponseTo = cxlRejResponseTo;
        this.cxlRejReason = cxlRejReason;
    }

    @Override
    public String toString() {
        return "37=" + orderId + FixMessage.delimiter +
                "11=" + clOrderId + FixMessage.delimiter +
                "41=" + origClientOrderId + FixMessage.delimiter +
                "39=" + orderStatus + FixMessage.delimiter +
                "434=" + cxlRejResponseTo + FixMessage.delimiter +
                "102=" + cxlRejReason + FixMessage.delimiter;
    }

    public static FixBodyCancelReject fromString(String str) {
        Map<String, String> map = new HashMap<>();
        String[] parts = str.split(FixMessage.delimiter);
        for (String part : parts) {
            String[] keyValue = part.split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException("FixBodyCancelReject is invalid");
            }
            map.put(keyValue[0], keyValue[1]);
        }

        return new FixBodyCancelReject(
                map.get("37"),
                map.get("11"),
                map.get("41"),
                OrderStatus.fromValue(map.get("39").charAt(0)),
                CxlRejResponseTo.fromString(map.get("434")),
                CxlRejReason.fromString(map.get("102"))
        );
    }
}
