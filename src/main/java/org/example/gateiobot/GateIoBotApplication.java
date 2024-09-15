package org.example.gateiobot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GateIoBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateIoBotApplication.class, args);
    }

}
