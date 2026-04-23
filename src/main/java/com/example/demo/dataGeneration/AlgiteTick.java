package com.example.demo.dataGeneration;

import com.example.demo.instrumentData.Instrument;
import com.zerodhatech.models.Tick;

import java.util.List;

public record  AlgiteTick (
    long instrumentToken,
    double ltp,
    long timestamp,
    long cumulativeVolume,
    String instrumentName
){
    public static AlgiteTick fromKiteTick(Tick tick, List<Instrument> atmOptions){
        Instrument instrument = atmOptions.stream()
                        .filter(i -> i.getInstrumentToken().equals(tick.getInstrumentToken()))
                        .findFirst()
                        .orElse(null);
        if(instrument==null){
            System.out.println("instrument is null");
        }
        return new AlgiteTick(tick.getInstrumentToken(),
                tick.getLastTradedPrice(),
                tick.getTickTimestamp().getTime(),
                tick.getVolumeTradedToday(),
                instrument.getTradingsymbol());
    }
}
