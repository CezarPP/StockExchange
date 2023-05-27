package org.common.fix.order;

public enum ExecTransType {
    New('0'),
    Cancel('1'),
    Correct('2'),
    Status('3');
    public final char label;

    ExecTransType(char label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return Character.toString(label);
    }
}
