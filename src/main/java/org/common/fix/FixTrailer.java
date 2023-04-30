package org.common.fix;

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

    static FixTrailer getTrailer(FixHeader fixHeader, FixBody fixBody) {
        String s = fixHeader.toString() + fixBody.toString();
        int ans = 0;
        for (int i = 0; i < s.length(); i++) {
            ans = (ans + s.charAt(i)) % 256;
        }
        return new FixTrailer(ans);
    }
}
