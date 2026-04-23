package com.example.demo.dataGeneration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class TickRouterConfig {
    @Bean
    public TickRouter tickRouter() {
        return new TickRouter();
    }
}
