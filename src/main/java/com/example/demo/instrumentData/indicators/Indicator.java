package com.example.demo.instrumentData.indicators;

import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;

public interface Indicator extends CandleConsumer {
    String name();
    Double getValue();
}
