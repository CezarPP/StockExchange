package org.common.fix.order;

public enum ExecTransType {
    New("0"),
    Cancel("1"),
    Correct("2"),
    Status("3");
    public final String label;

    ExecTransType(String label) {
        this.label = label;
    }
}
