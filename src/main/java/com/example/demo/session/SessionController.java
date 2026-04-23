package com.example.demo.session;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.LTPQuote;
import com.zerodhatech.models.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@EnableScheduling
@RestController
@RequestMapping("/algite")
public class SessionController {
    @Autowired
    ConnectionService connectionService;
    @Autowired
    AccessTokenRepo accessTokenRepo;
    @Autowired
    KiteConnect kiteConnect;

    @Value("${kite.api.secret}")
    private String apiSecret;


    @GetMapping("/connect")
    public ResponseEntity<String> connect(HttpServletResponse response) throws IOException {
        Optional<KiteSession> optionalKiteSession = accessTokenRepo.findById(1L);
        if(optionalKiteSession.isEmpty()){
            response.sendRedirect(kiteConnect.getLoginURL());
            return null;
        }
        if(!(optionalKiteSession.get().getTradingDay().isEqual(LocalDate.now()))){
            response.sendRedirect(kiteConnect.getLoginURL());
        }
        KiteSession session = optionalKiteSession.get();
        System.out.println(session.getAccessToken() + " ---" + session.getTradingDay());

        kiteConnect.setAccessToken(optionalKiteSession.get().getAccessToken());
        return new ResponseEntity<>("Alredy logged in for the day", HttpStatus.OK);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("request_token") String requestToken) throws Exception, KiteException {
        User user = kiteConnect.generateSession(requestToken, apiSecret);
        kiteConnect.setAccessToken(user.accessToken);
        connectionService.saveAccessToken(user.accessToken);
        return ResponseEntity.ok("Login successful for userId=" + user.userId + user.accessToken);
    }

    @GetMapping("/niftytest")
    public ResponseEntity<Double> fetchNiftyLTP() throws IOException, KiteException {
        String accessToken = connectionService.getAccessToken();
        kiteConnect.setAccessToken(accessToken);

        Map<String, LTPQuote> ltpMap = kiteConnect.getLTP(
                Collections.singletonList("NSE:NIFTY 50").toArray(new String[0])
        );

        LTPQuote nifty = ltpMap.get("NSE:NIFTY 50");

        if (nifty == null) {
            throw new RuntimeException("Failed to fetch NIFTY LTP");
        }

        System.out.println(nifty.lastPrice);
        ResponseEntity response = new ResponseEntity(nifty.lastPrice, HttpStatus.OK);
        return response;
    }


}
