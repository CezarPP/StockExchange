package org.common.fix;

public class FixMessage {
    static final String delimiter = "\001";
    private final FixHeader header;
    private final FixBody body;
    private final FixTrailer trailer;

    public FixMessage(FixHeader header, FixBody body, FixTrailer trailer) {
        this.header = header;
        this.body = body;
        this.trailer = trailer;
    }

/*    public FixMessage(String fixMessage) {
        String[] keyValues = fixMessage.split("\u0001");
        for(String keyValue : keyValues) {

        }
    }*/

    public FixHeader getHeader() {
        return header;
    }

    public FixBody getBody() {
        return body;
    }

    public FixTrailer getTrailer() {
        return trailer;
    }

    @Override
    public String toString() {
        return header.toString() +
                body.toString() +
                trailer.toString();
    }
}


