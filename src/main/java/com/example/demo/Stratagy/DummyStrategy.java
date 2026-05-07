package com.example.demo.Stratagy;

import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;
import com.example.demo.instrumentData.indicators.IndicatorSnapshot;
import com.example.demo.instrumentData.indicators.SmaIndicator;

public class DummyStrategy implements IndicatorConsumer {
    private final SmaIndicator sma20 = new SmaIndicator(20);

    @Override
    public void onIndicator(IndicatorSnapshot snapshot) {
        Double sma20 = snapshot.indicators().get("SMA_20");
        Double sma50 = snapshot.indicators().get("EMA_50");

        if (sma20 == null || sma50 == null) return;

        double close = snapshot.candle().close();

        System.out.println("SMA20: " + sma20);
        System.out.println("SMA50: " + sma50);
    }
}
