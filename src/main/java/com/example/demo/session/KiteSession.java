package com.example.demo.session;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class KiteSession {
    @Id
    private Long id = 1L;
    private String accessToken;
    private LocalDate tradingDay;
}
