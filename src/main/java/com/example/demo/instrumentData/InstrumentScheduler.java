package com.example.demo.instrumentData;

import com.example.demo.instrumentData.instrumentServices.InstrumentCacheService;
import com.example.demo.instrumentData.instrumentServices.InstrumentDownloadService;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InstrumentScheduler{

    private final InstrumentDownloadService downloader;
    private final InstrumentCacheService cache;

    public InstrumentScheduler(InstrumentDownloadService downloader,
                               InstrumentCacheService cache) {
        this.downloader = downloader;
        this.cache = cache;
    }

    @PostConstruct
    public void init() throws IOException {
        refresh();
    }

    @Scheduled(cron = "0 0 7 * * MON-FRI")
    public void refresh() throws IOException {
        downloader.downloadIfNeeded();
        cache.load();
    }
}
