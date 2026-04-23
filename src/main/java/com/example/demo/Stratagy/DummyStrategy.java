package com.example.demo.Stratagy;

import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;

public class DummyStrategy implements CandleConsumer {
    @Override
    public void onCandle(AlgiteCandle candle) {
        System.out.println(candle.instrumentToken() + ": " +candle.instrumentName() +": " + "open-" + candle.open()+
                "   high-" + candle.high()+
                "   low-" + candle.low()+
                "   close-" + candle.close());
    }
}
