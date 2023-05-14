package org.common.fix.body;

public class FixBodyLogout implements FixBody {

    @Override
    public String toString() {
        return "";
    }

    public static FixBodyLogout fromString(String str) {
        return new FixBodyLogout();
    }
}
