package com.example.demo.instrumentData.indicators;

import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;

import java.util.Optional;

public interface CandleIndicator {
    String name();
    void update(AlgiteCandle candle);
    Optional<Double> value();
}
