package com.example.demo.dataGeneration;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class TickRouter implements TickConsumer{
    private Map<Long, TickConsumer> consumersByInstrument;

    private final Map<Long, TickConsumer> consumers = new ConcurrentHashMap<>();

    public void register(long instrumentToken, TickConsumer consumer) {
        consumers.put(instrumentToken, consumer);
    }

    public void onTick(AlgiteTick tick) {
        TickConsumer consumer = consumers.get(tick.instrumentToken());
        if (consumer != null) {
            consumer.onTick(tick);
        }
    }


}
