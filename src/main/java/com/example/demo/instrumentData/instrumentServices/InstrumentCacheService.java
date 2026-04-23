package com.example.demo.instrumentData.instrumentServices;


import com.example.demo.instrumentData.Instrument;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class InstrumentCacheService {

    private final Map<Long, Instrument> byToken = new ConcurrentHashMap<>();
    private final List<Instrument> all = new CopyOnWriteArrayList<>();

    public void load() throws IOException {

        byToken.clear();
        all.clear();

        try (Reader reader = Files.newBufferedReader(Paths.get("data/instruments.csv"));
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            for (CSVRecord r : csvParser) {

                String exchange = r.get("exchange");
                String segment = r.get("segment");
                String name = r.get("name");
                String instrumentType = r.get("instrument_type");

                if (!"NFO".equals(exchange)) continue;
                if (!"NFO-OPT".equals(segment)) continue;
                if (!"NIFTY".equals(name)) continue;
                if (!("CE".equals(instrumentType) || "PE".equals(instrumentType))) continue;

                Instrument inst = new Instrument(
                        Long.parseLong(r.get("instrument_token")),
                        r.get("tradingsymbol"),
                        name,
                        exchange,
                        segment,
                        LocalDate.parse(r.get("expiry")),
                        Double.parseDouble(r.get("strike")),
                        instrumentType
                );

                byToken.put(inst.getInstrumentToken(), inst);
                all.add(inst);
            }
        }

        System.out.println("Loaded instruments: " + all.size());
    }

    public List<Instrument> getAll() {
        return all;
    }

    public Instrument getByToken(Long token) {
        return byToken.get(token);
    }
}
