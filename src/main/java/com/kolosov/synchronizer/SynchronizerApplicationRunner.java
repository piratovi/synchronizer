package com.kolosov.synchronizer;

import com.kolosov.synchronizer.service.SyncService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@AllArgsConstructor
@SpringBootApplication
public class SynchronizerApplicationRunner implements CommandLineRunner {

    private final SyncService syncService;

    public static void main(String[] args) {
        SpringApplication.run(SynchronizerApplicationRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        syncService.autoSynchronization();
    }

}
