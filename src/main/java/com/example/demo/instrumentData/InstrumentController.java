package com.example.demo.instrumentData;

import com.example.demo.instrumentData.instrumentServices.AlgiteTickerService;
import com.example.demo.instrumentData.instrumentServices.SessionNotFoundException;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableScheduling
@RequestMapping("/algite")
public class InstrumentController {

    @Autowired
    AlgiteTickerService algiteTickerService;

    @GetMapping("/ticker")
    public void startStream() throws KiteException, Exception, SessionNotFoundException {
        algiteTickerService.connect();
    }
}
