package org.common.fix.header;

public enum BeginString {
    Fix_4_4("FIX.4.4");
    public final String label;

    BeginString(String label) {
        this.label = label;
    }
}
