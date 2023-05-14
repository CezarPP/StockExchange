package org.common.fix.body;

import org.common.fix.FixMessage;
import org.common.fix.login.EncryptMethod;

public class FixBodyLogin implements FixBody {
    /**
     * 98 -> EncryptMethod -> FIX.4.4 -> Always unencrypted
     */
    final EncryptMethod encryptMethod;
    /**
     * 108 -> HeartBtInt -> FIX.4.4 -> Same value used by both sides
     */
    final int heartBeat;

    /**
     * 554 -> Username -> FIX.4.4 -> Userid or username.
     */
    final String username;


    public FixBodyLogin(EncryptMethod encryptMethod, int heartBeat, String username) {
        this.encryptMethod = encryptMethod;
        this.heartBeat = heartBeat;
        this.username = username;
    }

    @Override
    public String toString() {
        return "98=" + encryptMethod + FixMessage.delimiter +
                "108=" + heartBeat + FixMessage.delimiter +
                "554=" + username + FixMessage.delimiter;
    }

    public static FixBodyLogin fromString(String str) {
        String[] parts = str.split(FixMessage.delimiter);

        EncryptMethod encryptMethod = null;
        int heartBeat = 0;
        String username = null;

        for (String part : parts) {
            String[] keyValue = part.split("=");
            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "98" -> encryptMethod = EncryptMethod.fromValue(Integer.parseInt(value));
                case "108" -> heartBeat = Integer.parseInt(value);
                case "554" -> username = value;
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            }
        }

        if (encryptMethod == null || heartBeat == 0) {
            throw new IllegalArgumentException("Required fields missing");
        }

        return new FixBodyLogin(encryptMethod, heartBeat, username);
    }
}


