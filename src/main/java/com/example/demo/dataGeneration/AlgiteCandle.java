package com.example.demo.dataGeneration;

public record AlgiteCandle(long instrumentToken,
                           String instrumentName,
                           long startTime,
                           double open,
                           double high,
                           double low,
                           double close,
                           long volume) {
    public static AlgiteCandle fromTick(AlgiteTick tick, long startTime) {
        double price = tick.ltp();
        return new AlgiteCandle(
                tick.instrumentToken(),
                tick.instrumentName(),
                startTime,
                price,
                price,
                price,
                price,
                0L
        );
    }

    public AlgiteCandle update(double price, long volumeDelta) {
        return new AlgiteCandle(
                instrumentToken,
                instrumentName,
                startTime,
                open,
                Math.max(high, price),
                Math.min(low, price),
                price,
                volume + volumeDelta
        );
    }

}
