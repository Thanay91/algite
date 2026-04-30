package com.example.demo.Stratagy;

import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;
import com.example.demo.instrumentData.indicators.SmaIndicator;

public class DummyStrategy implements CandleConsumer {
    private final SmaIndicator sma20 = new SmaIndicator(20);

    @Override
    public void onCandle(AlgiteCandle candle) {
        sma20.onCandle(candle);

        System.out.println(candle.instrumentToken() + ": " +candle.instrumentName() +": " + "open-" + candle.open()+
                "   high-" + candle.high()+
                "   low-" + candle.low()+
                "   close-" + candle.close()+
                "    sma-" + sma20.getValue());
    }
}
