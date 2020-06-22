package com.kolosov.synchronizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class SynchronizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynchronizerApplication.class, args);
    }

}
