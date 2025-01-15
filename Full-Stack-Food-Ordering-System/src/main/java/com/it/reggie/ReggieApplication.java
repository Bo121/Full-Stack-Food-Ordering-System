package com.it.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j // 1. We can directly use "log"
@SpringBootApplication
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);

        // 1. Thanks to @Slf4j, displayed in "console"
        log.info("Application started");
    }
}
