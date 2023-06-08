package org.common.fix.body;

import org.common.fix.FixMessage;

public class FixBodyLogout implements FixBody {

    /**
     * 58 -> Text -> FIX.4.4 -> Free format text string.
     */
    private final String text;

    public FixBodyLogout(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "58=" + text + FixMessage.delimiter;
    }

    public static FixBodyLogout fromString(String str) {
        String[] parts = str.split(FixMessage.delimiter);
        String text = null;

        for (String part : parts) {
            String[] keyValue = part.split("=");
            String key = keyValue[0];
            String value = keyValue[1];

            if (key.equals("58")) {
                text = value;
            } else {
                throw new IllegalArgumentException("Unknown field: " + key);
            }
        }

        if (text == null) {
            throw new IllegalArgumentException("Required field missing");
        }

        return new FixBodyLogout(text);
    }
}