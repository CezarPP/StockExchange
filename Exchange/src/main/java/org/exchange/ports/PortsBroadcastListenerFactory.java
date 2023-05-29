package org.exchange.ports;

public class PortsBroadcastListenerFactory {
    private static PortsBroadcastListener portsBroadcastListener = null;

    public static PortsBroadcastListener getInstance() {
        if (portsBroadcastListener == null) {
            portsBroadcastListener = new PortsBroadcastListener();
            portsBroadcastListener.start();
        }
        return portsBroadcastListener;
    }
}
