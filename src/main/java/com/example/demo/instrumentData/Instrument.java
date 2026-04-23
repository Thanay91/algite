package com.example.demo.instrumentData;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Instrument {
    private Long instrumentToken;
    private String tradingsymbol;
    private String name;
    private String exchange;
    private String segment;
    private LocalDate expiry;
    private double strike;
    private String instrumentType;

    public Instrument(Long instrumentToken, String tradingsymbol, String name,
                      String exchange, String segment, LocalDate expiry,
                      double strike, String instrumentType) {
        this.instrumentToken = instrumentToken;
        this.tradingsymbol = tradingsymbol;
        this.name = name;
        this.exchange = exchange;
        this.segment = segment;
        this.expiry = expiry;
        this.strike = strike;
        this.instrumentType = instrumentType;
    }
}
