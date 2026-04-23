package com.example.demo.session;

import com.zerodhatech.kiteconnect.KiteConnect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

    @Value("${kite.api.key}")
    private String apiKey;

    @Bean
    public KiteConnect kiteConnect(){
        return new KiteConnect(apiKey);
    }

}
