package com.example.demo.instrumentData.indicators;

import com.example.demo.Stratagy.IndicatorConsumer;
import com.example.demo.dataGeneration.AlgiteCandle;

import java.util.*;

public class SmaIndicator implements CandleIndicator {

    private final int period;
    private final Queue<Double> closes = new LinkedList<>();
    private double sum = 0;

    public SmaIndicator(int period) {
        this.period = period;
    }

    @Override
    public String name() {
        return "SMA_" + period;
    }

    @Override
    public void update(AlgiteCandle candle) {
        closes.add(candle.close());
        sum += candle.close();

        if (closes.size() > period) {
            sum -= closes.poll();
        }
        System.out.println(candle.instrumentName() + ": " + sum);
    }

    @Override
    public Optional<Double> value() {
        if (closes.size() < period) {
            return Optional.empty();
        }
        return Optional.of(sum / period);
    }
}
