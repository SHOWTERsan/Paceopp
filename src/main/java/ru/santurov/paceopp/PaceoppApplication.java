package ru.santurov.paceopp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaceoppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaceoppApplication.class, args);
    }

}
