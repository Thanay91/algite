package com.example.demo.dataGeneration;

import com.example.demo.Stratagy.IndicatorConsumer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class CandleBuilder implements TickConsumer {
    private final long timeframeMillis;
    private AlgiteCandle current;
    private final List<CandleConsumer> consumers;
    private long lastCumVolume = 0;
    private final long instrumentToken;

    public CandleBuilder(long timeframeMillis, List<CandleConsumer> consumers, long instrumentToken) {
        this.timeframeMillis = timeframeMillis;
        this.consumers = consumers;
        this.instrumentToken=  instrumentToken;
    }

    @Override
    public void onTick(AlgiteTick tick) {
//        System.out.println("Building candle for " + tick.instrumentToken());
        if (tick.instrumentToken() != instrumentToken) {
            return; // or throw
        }
        long candleStart = (tick.timestamp() / timeframeMillis) * timeframeMillis;

        if (current == null || candleStart > current.startTime()) {
            // Close the previous candle
            closeCurrentCandle();

            // Start a new candle
            current = AlgiteCandle.fromTick(tick, candleStart);

            // Initialize last cumulative volume
            lastCumVolume = tick.cumulativeVolume();
        } else {
            // Update the existing candle
            updateCurrentCandle(tick);
        }
    }

    private void updateCurrentCandle(AlgiteTick tick) {
        // Update OHLC
        long volumeDelta = Math.max(
                0,
                tick.cumulativeVolume() - lastCumVolume
        );
        current = current.update(tick.ltp(), volumeDelta);
        lastCumVolume = tick.cumulativeVolume();

    }

    private void closeCurrentCandle() {
        if (current != null) {
            System.out.println(current.instrumentName() + " close : " + current.close());
            consumers.forEach(c -> c.onCandle(current));
        }
    }

    private void register(CandleConsumer consumer){
        this.consumers.add(consumer);
    }
}
