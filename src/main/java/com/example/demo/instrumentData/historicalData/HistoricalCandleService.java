package com.example.demo.instrumentData.historicalData;

import com.example.demo.dataGeneration.AlgiteCandle;
import com.example.demo.dataGeneration.CandleConsumer;
import com.example.demo.instrumentData.Instrument;
import com.example.demo.instrumentData.instrumentServices.InstrumentQueryService;
import com.example.demo.session.AccessTokenRepo;
import com.example.demo.session.KiteSession;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import org.springframework.stereotype.Service;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class HistoricalCandleService {


    private final KiteConnect kiteConnect;
    private final AccessTokenRepo accessTokenRepo;
    private final InstrumentQueryService instrumentQueryService;

    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    public HistoricalCandleService(
            KiteConnect kiteConnect,
            AccessTokenRepo accessTokenRepo,
            InstrumentQueryService instrumentQueryService
    ) {
        this.kiteConnect = kiteConnect;
        this.accessTokenRepo = accessTokenRepo;
        this.instrumentQueryService = instrumentQueryService;
    }

    public Map<Long, List<AlgiteCandle>> getNiftyHistoricalCandles(
            LocalDate date,
            double niftySpot,
            double range
    ) throws Exception, KiteException {

        ensureValidSession();

        LocalDate expiry = instrumentQueryService.getNearestExpiry();

        List<Instrument> instruments =
                instrumentQueryService.getNiftyATMOptions(expiry, niftySpot, range);

        ZonedDateTime from = ZonedDateTime.of(date, LocalTime.of(9, 15), IST);
        ZonedDateTime to = ZonedDateTime.of(date, LocalTime.of(15, 30), IST);


        Map<Long, List<AlgiteCandle>> candleMap = new LinkedHashMap<>();

        for (Instrument instrument : instruments) {

            HistoricalData historicalData = kiteConnect.getHistoricalData(
                    Date.from(from.toInstant()),
                    Date.from(to.toInstant()),
                    String.valueOf(instrument.getInstrumentToken()),
                    "minute",
                    false,
                    false
            );

            List<AlgiteCandle> candles = new ArrayList<>();

            if (historicalData.dataArrayList != null) {
                for (HistoricalData data : historicalData.dataArrayList) {
                    candles.add(new AlgiteCandle(
                            instrument.getInstrumentToken(),
                            instrument.getTradingsymbol(),
                            parseTimestamp(data.timeStamp),
                            data.open,
                            data.high,
                            data.low,
                            data.close,
                            data.volume
                    ));
                }
            }

            candleMap.put(instrument.getInstrumentToken(), candles);
        }

        return candleMap;
    }

    public void replayHistoricalCandles(
            LocalDate date,
            double niftySpot,
            double range,
            CandleConsumer candleConsumer
    ) throws Exception, KiteException {

        Map<Long, List<AlgiteCandle>> candleMap =
                getNiftyHistoricalCandles(date, niftySpot, range);

        List<AlgiteCandle> mergedCandles = candleMap.values()
                .stream()
                .flatMap(List::stream)
                .sorted(
                        Comparator.comparingLong(AlgiteCandle::startTime)
                                .thenComparingLong(AlgiteCandle::instrumentToken)
                )
                .toList();

        int speedFactor = 60; // 1 minute becomes 1 second

        AlgiteCandle previous = null;

        for (AlgiteCandle candle : mergedCandles) {

            if (previous != null) {
                long gapMillis = candle.startTime() - previous.startTime();

                if (gapMillis > 0) {
                    long sleepMillis = gapMillis / speedFactor;
                    Thread.sleep(Math.max(1, sleepMillis));
                }
            }

            candleConsumer.onCandle(candle);
            previous = candle;
        }
    }

    private void ensureValidSession() {
        Optional<KiteSession> optionalSession = accessTokenRepo.findById(1L);

        if (optionalSession.isEmpty()) {
            throw new IllegalStateException("No Kite session found. Login first.");
        }

        KiteSession session = optionalSession.get();

        if (session.getTradingDay() == null ||
                !session.getTradingDay().isEqual(LocalDate.now(IST))) {
            throw new IllegalStateException("Kite session expired. Login again.");
        }

        kiteConnect.setAccessToken(session.getAccessToken());
    }

    private long parseTimestamp(String timestamp) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
        return ZonedDateTime.parse(timestamp, formatter)
                .toInstant()
                .toEpochMilli();
    }
}
