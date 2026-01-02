package com.tennistournament;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TennisTournamentApplication {

    public static void main(String[] args) {
        SpringApplication.run(TennisTournamentApplication.class, args);
    }
}

