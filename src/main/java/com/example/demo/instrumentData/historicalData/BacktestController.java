package com.example.demo.instrumentData.historicalData;
import com.example.demo.dataGeneration.CandleConsumer;
import com.example.demo.dataGeneration.AlgiteCandle;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/backtest")
public class BacktestController {

    private final HistoricalCandleService historicalService;

    public BacktestController(HistoricalCandleService historicalService) {
        this.historicalService = historicalService;
    }

    @GetMapping("/nifty-range")
    public String testBacktest(
            @RequestParam String date,
            @RequestParam double spot,
            @RequestParam(defaultValue = "100") double range
    ) throws Exception, KiteException {

        LocalDate backtestDate = LocalDate.parse(date);

        historicalService.replayHistoricalCandles(
                backtestDate,
                spot,
                range,
                new CandleConsumer() {
                    @Override
                    public void onCandle(AlgiteCandle candle) {
                        System.out.println(
                                candle.instrumentName() + " | " +
                                        candle.startTime() + " | " +
                                        candle.open() + " " +
                                        candle.high() + " " +
                                        candle.low() + " " +
                                        candle.close()

                        );
                    }
                }
        );

        return "Backtest completed. Check console logs.";
    }
}