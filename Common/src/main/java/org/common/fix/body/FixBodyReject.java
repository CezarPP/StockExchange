package org.common.fix.body;

import org.common.fix.FixMessage;

public class FixBodyReject implements FixBody {
    /**
     * 45 -> RefSeqNum -> MsgSeqNum <34> of rejected message
     */
    final int refSeqNum;

    FixBodyReject(int refSeqNum) {
        this.refSeqNum = refSeqNum;
    }

    @Override
    public String toString() {
        return "45=" + refSeqNum + FixMessage.delimiter;
    }

    public static FixBodyReject fromString(String str) {
        String[] parts = str.split("=");
        assert parts.length == 2;

        return new FixBodyReject(Integer.parseInt(parts[1]));
    }

}
