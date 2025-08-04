package br.com.a3sitsolutions.tempo_box.controller;

import br.com.a3sitsolutions.tempo_box.dto.FileDescriptionDto;
import br.com.a3sitsolutions.tempo_box.dto.FileUploadResponse;
import br.com.a3sitsolutions.tempo_box.entity.FileMetadata;
import br.com.a3sitsolutions.tempo_box.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileApiController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("storageDurationMinutes") Integer storageDurationMinutes,
            @RequestParam("authToken") String authToken,
            @RequestParam(value = "accessToken", required = false) String accessToken,
            @RequestParam(value = "repository", required = false) String repository,
            @RequestParam(value = "commit", required = false) String commit,
            @RequestParam(value = "commitMessage", required = false) String commitMessage,
            @RequestParam(value = "branch", required = false) String branch,
            @RequestParam(value = "runId", required = false) String runId,
            @RequestParam(value = "actor", required = false) String actor,
            @RequestParam(value = "apkName", required = false) String apkName,
            @RequestParam(value = "downloadUrl", required = false) String downloadUrl) {

        try {
            FileDescriptionDto description = new FileDescriptionDto(
                    repository, commit, commitMessage, branch, runId, actor, apkName, downloadUrl
            );

            FileUploadResponse response = fileStorageService.uploadFile(
                    file, storageDurationMinutes, accessToken, authToken, description
            );

            return ResponseEntity.ok(response);

        } catch (SecurityException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);

        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Upload failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(
            @PathVariable String fileId,
            @RequestHeader("Access-Token") String accessToken) {

        Optional<FileMetadata> metadataOpt = fileStorageService.getFileMetadata(fileId, accessToken);

        if (metadataOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "Invalid file ID or access token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        FileMetadata metadata = metadataOpt.get();

        if (metadata.isExpired()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File Expired");
            error.put("message", "The requested file has expired and is no longer available");
            error.put("expiredAt", metadata.getExpiresAt().toString());
            return ResponseEntity.status(HttpStatus.GONE).body(error);
        }

        Optional<File> fileOpt = fileStorageService.getFile(fileId, accessToken);

        if (fileOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "File Not Found");
            error.put("message", "The requested file could not be found on the server");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        File file = fileOpt.get();
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + metadata.getOriginalFilename() + "\"")
                .contentType(MediaType.parseMediaType(metadata.getContentType()))
                .contentLength(file.length())
                .body(resource);
    }
}