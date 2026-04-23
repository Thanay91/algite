package com.example.demo.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ConnectionService {

    @Autowired
    private AccessTokenRepo accessTokenRepo;

    public void saveAccessToken(String accessToken){
        KiteSession kiteSession = new KiteSession();
        kiteSession.setAccessToken(accessToken);
        kiteSession.setTradingDay(LocalDate.now());
        kiteSession.setId(1L);
        accessTokenRepo.save(kiteSession);
    }

    public String getAccessToken(){
        Optional<KiteSession> optionalKiteSession = accessTokenRepo.findById(1L);
        String accessToken = optionalKiteSession.get().getAccessToken();
        return accessToken;
    }
}
