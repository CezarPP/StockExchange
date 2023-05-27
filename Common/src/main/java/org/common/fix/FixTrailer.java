package org.common.fix;

import org.common.fix.body.FixBody;
import org.common.fix.header.FixHeader;

/**
 * Just the checksum -> always unencrypted
 * The checksum is calculated as the sum of all the bytes up to but not including the checksum field itself
 * Optionally: could also include SignatureLength and Signature
 */
public record FixTrailer(int checkSum) {

    @Override
    public String toString() {
        return "10=" + checkSum + FixMessage.delimiter;
    }

    public static FixTrailer getTrailer(FixHeader fixHeader, FixBody fixBody) {
        String s = fixHeader.toString() + fixBody.toString();
        int ans = 0;
        for (int i = 0; i < s.length(); i++) {
            ans = (ans + s.charAt(i)) % 256;
        }
        return new FixTrailer(ans);
    }

    public static FixTrailer fromString(String str) {
        String[] parts = str.split(FixMessage.delimiter)[0].split("=");
        if (parts.length != 2 || !parts[0].equals("10")) {
            throw new IllegalArgumentException("Invalid FIX trailer: " + str);
        }
        return new FixTrailer(Integer.parseInt(parts[1]));
    }

    public static boolean verifyChecksum(FixHeader fixHeader, FixBody fixBody, FixTrailer fixTrailer) {
        String s = fixHeader.toString() + fixBody.toString();
        int checksum = 0;
        for (int i = 0; i < s.length(); i++) {
            checksum = (checksum + s.charAt(i)) % 256;
        }
        return checksum == fixTrailer.checkSum;
    }
}
