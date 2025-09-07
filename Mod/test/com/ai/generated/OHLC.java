package com.ai.generated;

public class OHLC {
    String timestamp; // Minute-level timestamp
    double open, high, low, close, volume;

    public OHLC(String timestamp, double open, double high, double low, double close, double volume) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

}
