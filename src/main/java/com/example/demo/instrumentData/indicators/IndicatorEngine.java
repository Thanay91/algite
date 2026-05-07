package com.example.demo.instrumentData.indicators;

import com.example.demo.Stratagy.IndicatorConsumer;
import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndicatorEngine implements CandleConsumer {

    private final List<CandleIndicator> indicators;
    private final List<IndicatorConsumer> strategies;

    public IndicatorEngine(
            List<CandleIndicator> indicators,
            List<IndicatorConsumer> strategies
    ) {
        this.indicators = indicators;
        this.strategies = strategies;
    }

    @Override
    public void onCandle(AlgiteCandle candle) {

        Map<String, Double> values = new HashMap<>();

        for (CandleIndicator indicator : indicators) {
            indicator.update(candle);
            indicator.value().ifPresent(v ->
                    values.put(indicator.name(), v)
            );
        }

        IndicatorSnapshot snapshot =
                new IndicatorSnapshot(candle, values);

        for (IndicatorConsumer strategy : strategies) {
            strategy.onIndicator(snapshot);
        }
    }
}
