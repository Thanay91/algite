package com.example.demo.instrumentData.instrumentServices;


import com.example.demo.Stratagy.DummyStrategy;
import com.example.demo.dataGeneration.AlgiteTick;
import com.example.demo.dataGeneration.CandleBuilder;
import com.example.demo.dataGeneration.TickRouter;
import com.example.demo.instrumentData.Instrument;
import com.example.demo.instrumentData.indicators.IndicatorEngine;
import com.example.demo.instrumentData.indicators.SmaIndicator;
import com.example.demo.session.AccessTokenRepo;
import com.example.demo.session.KiteSession;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Quote;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class AlgiteTickerService {

    @Autowired
    private KiteConnect kiteConnect;

    @Autowired
    private AccessTokenRepo repo;

    @Autowired
    private InstrumentQueryService instrumentQueryService;

    @Autowired
    TickRouter tickRouter;

    private Long subscribedTokens;


    public synchronized void connect() throws KiteException, Exception, SessionNotFoundException {

        Optional<KiteSession> kiteSession = repo.findById(1L);
        if (kiteSession.isEmpty() || !(kiteSession.get().getTradingDay().isEqual(LocalDate.now()))) {
            throw new SessionNotFoundException("Not logged in yet, Please log in");
        }
        kiteConnect.setAccessToken(kiteSession.get().getAccessToken());

        String accessToken = kiteConnect.getAccessToken();
        String apiKey = kiteConnect.getApiKey();
        KiteTicker kiteTicker = new KiteTicker(accessToken, apiKey);


        LocalDate expiry = instrumentQueryService.getNearestExpiry();
        double spot = getCurrentNiftyLTP(); // or get from live NIFTY price

        List<Instrument> atmOptions = instrumentQueryService.getNiftyATMOptions(expiry, spot, 50);

        // 🔹 On connect
        kiteTicker.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                System.out.println("KiteTicker connected");
                ArrayList<Long> subscribedTokens = atmOptions.stream()
                        .map(Instrument::getInstrumentToken)
                        .collect(Collectors.toCollection(ArrayList::new));

                System.out.println("Subscribing tokens: " + subscribedTokens);
                long ONE_MIN = 60_000L;
                IndicatorEngine engine = new IndicatorEngine(
                        List.of(new SmaIndicator(20), new SmaIndicator(50)),
                        List.of(new DummyStrategy())
                );
                for (long token : subscribedTokens) {
                    CandleBuilder builder = new CandleBuilder(
                            ONE_MIN,
                            List.of(engine),
                            token
                    );
                    tickRouter.register(token, builder);
                }

                kiteTicker.subscribe(subscribedTokens);
                kiteTicker.setMode(subscribedTokens, KiteTicker.modeFull);
            }
        });

        // 🔹 On ticks
        kiteTicker.setOnTickerArrivalListener(ticks -> {
            for (Tick tick : ticks) {
                AlgiteTick algiteTick = AlgiteTick.fromKiteTick(tick, atmOptions);
                tickRouter.onTick(algiteTick);
//
                // Find trading symbol from your cached instruments
//                Instrument inst = atmOptions.stream()
//                        .filter(i -> i.getInstrumentToken().equals(token))
//                        .findFirst()
//                        .orElse(null);
////
//                if (inst != null) {
//                    System.out.println("Tick: " + inst.getTradingsymbol() +
//                            ", Token: " + token +
//                            ", LTP: " + ltp +
//                            ", Volume: " + volTraded);
//                }
//            }

//                for (Tick kiteTick : ticks) {
//                    AlgiteTick algiteTick = AlgiteTick.fromKiteTick(kiteTick, atmOptions);
//                    tickRouter.onTick(algiteTick);
//                }
            }
        });


        // 🔹 On disconnect
        kiteTicker.setOnDisconnectedListener(new OnDisconnect() {
            @Override
            public void onDisconnected() {
                System.out.println("KiteTicker disconnected");
            }
        });
        System.out.println("AccessToken = " + kiteConnect.getAccessToken());


        kiteTicker.connect();
    }


    public double getCurrentNiftyLTP() throws Exception, KiteException {
        // NIFTY 50 index token on NSE
        String[] instrumentToken = new String[1];  // You can also use Kite instrument token map
        instrumentToken[0] =  "NSE:NIFTY 50";
        // Fetch the quote
        Map<String, Quote> quote = kiteConnect.getQuote(instrumentToken);

        // Return last price
        return quote.get("NSE:NIFTY 50").lastPrice;
    }
}
