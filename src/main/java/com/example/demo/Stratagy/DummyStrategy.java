package com.example.demo.Stratagy;

import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;
import com.example.demo.instrumentData.indicators.IndicatorSnapshot;
import com.example.demo.instrumentData.indicators.SmaIndicator;

public class DummyStrategy implements IndicatorConsumer {

    @Override
    public void onIndicator(IndicatorSnapshot snapshot) {

        Double sma20 = snapshot.indicators().get("SMA_20");
        Double sma50 = snapshot.indicators().get("SMA_50");

        if (sma20 == null || sma50 == null) {
            return;
        }

        if (sma20 > sma50) {
            System.out.println("BUY signal");
        } else if (sma20 < sma50) {
            System.out.println("SELL signal");
        }
    }
}
