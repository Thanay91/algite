package com.example.demo.instrumentData.indicators;

import com.example.demo.dataGeneration.AlgiteCandle;

import java.util.Map;

public record IndicatorSnapshot(
    AlgiteCandle candle,
    Map<String, Double> indicators
){}
