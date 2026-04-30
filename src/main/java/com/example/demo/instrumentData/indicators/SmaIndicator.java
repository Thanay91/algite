package com.example.demo.instrumentData.indicators;

import com.example.demo.dataGeneration.AlgiteCandle;

import java.util.LinkedList;
import java.util.Queue;

public class SmaIndicator implements Indicator{
    private final int period;
    private final Queue<Double> closes = new LinkedList<>();
    private double sum = 0.0;
    private Double value = null;

    public SmaIndicator(int period) {
        this.period = period;
    }

    @Override
    public void onCandle(AlgiteCandle candle) {
        double close = candle.close();

        closes.add(close);
        sum += close;

        if (closes.size() > period) {
            sum -= closes.poll();
        }

        if (closes.size() == period) {
            value = sum / period;
        }
    }

    @Override
    public Double getValue() {
        return value;
    }
}
