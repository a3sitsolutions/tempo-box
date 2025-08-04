package br.com.a3sitsolutions.tempo_box.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileCleanupService {

    private final FileStorageService fileStorageService;

    @Scheduled(fixedRateString = "${app.cleanup.interval}")
    public void cleanupExpiredFiles() {
        log.info("Starting cleanup of expired files...");
        try {
            fileStorageService.cleanupExpiredFiles();
            log.info("Cleanup of expired files completed successfully");
        } catch (Exception e) {
            log.error("Error during file cleanup: ", e);
        }
    }
}