package com.example.demo.instrumentData.instrumentServices;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class InstrumentDownloadService {

    private static final String INSTRUMENT_URL =
            "https://api.kite.trade/instruments";

    private static final Path FILE_PATH =
            Paths.get("data/instruments.csv");

    public void downloadIfNeeded() throws IOException {

        if (Files.exists(FILE_PATH)) {
            LocalDate lastModified =
                    Files.getLastModifiedTime(FILE_PATH)
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

            if (lastModified.equals(LocalDate.now())) {
                return; // already downloaded today
            }
        }

        Files.createDirectories(FILE_PATH.getParent());

        try (InputStream in = new URL(INSTRUMENT_URL).openStream()) {
            Files.copy(in, FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("Instruments downloaded");
    }
}