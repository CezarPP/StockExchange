package org.client;

import java.util.Timer;
import java.util.TimerTask;

public class RequestTimer {
    private static final int period = 1_000; // 1 second
    private final TimerObserver observer;
    private Timer timer;

    public RequestTimer(TimerObserver observer) {
        this.timer = new Timer();
        this.observer = observer;
    }

    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                observer.requestMarketData();
            }
        }, 0, period);
    }
}
