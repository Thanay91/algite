package com.example.demo.instrumentData.instrumentServices;

import com.example.demo.instrumentData.Instrument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class InstrumentQueryService {

    @Autowired
    private InstrumentCacheService cache;


    public List<Instrument> getNiftyOptions(
            LocalDate expiry, double strike) {

        return cache.getAll().stream()
                .filter(i -> expiry.equals(i.getExpiry()))
                .filter(i -> i.getStrike() == strike)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public LocalDate getNearestExpiry() {
        return cache.getAll().stream()
                .filter(i -> "NIFTY".equals(i.getName()))
                .filter(i -> "NFO-OPT".equals(i.getSegment()))
                .map(Instrument::getExpiry)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElseThrow(() -> new IllegalStateException("No NIFTY expiry found"));
    }

    public List<Instrument> getNiftyOptionsForExpiry(LocalDate expiry) {
        return cache.getAll().stream()
                .filter(i -> "NIFTY".equals(i.getName()))
                .filter(i -> "NFO-OPT".equals(i.getSegment()))
                .filter(i -> expiry.equals(i.getExpiry()))
                .filter(i -> "CE".equals(i.getInstrumentType()) ||
                        "PE".equals(i.getInstrumentType()))
                .toList();
    }

    public List<Instrument> getNiftyATMOptions(LocalDate expiry, double spot, double range) {
        return cache.getAll().stream()
                .filter(i -> "NIFTY".equals(i.getName()))
                .filter(i -> "NFO-OPT".equals(i.getSegment()))
                .filter(i -> expiry.equals(i.getExpiry()))
                .filter(i -> "CE".equals(i.getInstrumentType()) || "PE".equals(i.getInstrumentType()))
                .filter(i -> Math.abs(i.getStrike() - spot) <= range) // ±range filter
                .toList();
    }


}
