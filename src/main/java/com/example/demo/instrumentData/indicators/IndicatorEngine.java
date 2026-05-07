package com.example.demo.instrumentData.indicators;

import com.example.demo.Stratagy.IndicatorConsumer;
import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndicatorEngine implements CandleConsumer {

    private final List<Indicator> indicators;
    private final List<IndicatorConsumer> consumers;

    public IndicatorEngine(
            List<Indicator> indicators,
            List<IndicatorConsumer> consumers
    ) {
        this.indicators = indicators;
        this.consumers = consumers;
    }

    @Override
    public void onCandle(AlgiteCandle candle) {

        Map<String, Double> values = new HashMap<>();

        for (Indicator indicator : indicators) {
            indicator.onCandle(candle);
            values.put(indicator.name(), indicator.getValue());
        }

        IndicatorSnapshot snapshot = new IndicatorSnapshot(
                candle,
                values
        );

        for (IndicatorConsumer consumer : consumers) {
            consumer.onIndicator(snapshot);
        }
    }
}
