package br.com.a3sitsolutions.tempo_box.service;

import br.com.a3sitsolutions.tempo_box.dto.FileDescriptionDto;
import br.com.a3sitsolutions.tempo_box.dto.FileUploadResponse;
import br.com.a3sitsolutions.tempo_box.entity.FileDescription;
import br.com.a3sitsolutions.tempo_box.entity.FileMetadata;
import br.com.a3sitsolutions.tempo_box.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileMetadataRepository fileMetadataRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.auth.static-token}")
    private String staticAuthToken;

    public FileUploadResponse uploadFile(
            MultipartFile file, 
            Integer storageDurationMinutes, 
            String accessToken, 
            String authToken,
            FileDescriptionDto descriptionDto) throws IOException {

        if (!staticAuthToken.equals(authToken)) {
            throw new SecurityException("Invalid authentication token");
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileId = UUID.randomUUID().toString();
        String generatedAccessToken = accessToken != null ? accessToken : UUID.randomUUID().toString();
        String storedFilename = fileId + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(storedFilename);

        Files.copy(file.getInputStream(), filePath);

        FileDescription description = new FileDescription();
        if (descriptionDto != null) {
            description.setRepository(descriptionDto.getRepository());
            description.setCommit(descriptionDto.getCommit());
            description.setCommitMessage(descriptionDto.getCommitMessage());
            description.setBranch(descriptionDto.getBranch());
            description.setRunId(descriptionDto.getRunId());
            description.setActor(descriptionDto.getActor());
            description.setApkName(descriptionDto.getApkName());
            description.setDownloadUrl(descriptionDto.getDownloadUrl());
        }

        FileMetadata metadata = new FileMetadata();
        metadata.setFileId(fileId);
        metadata.setOriginalFilename(file.getOriginalFilename());
        metadata.setStoredFilename(storedFilename);
        metadata.setFileSize(file.getSize());
        metadata.setContentType(file.getContentType());
        metadata.setAccessToken(generatedAccessToken);
        metadata.setAuthToken(authToken);
        metadata.setStorageDurationMinutes(storageDurationMinutes);
        metadata.setCreatedAt(LocalDateTime.now());
        metadata.setExpiresAt(LocalDateTime.now().plusMinutes(storageDurationMinutes));
        metadata.setDescription(description);

        fileMetadataRepository.save(metadata);

        return new FileUploadResponse(
                fileId, 
                generatedAccessToken, 
                "File uploaded successfully", 
                storageDurationMinutes.longValue()
        );
    }

    public Optional<FileMetadata> getFileMetadata(String fileId, String accessToken) {
        return fileMetadataRepository.findByFileIdAndAccessToken(fileId, accessToken);
    }

    public Optional<File> getFile(String fileId, String accessToken) {
        Optional<FileMetadata> metadataOpt = getFileMetadata(fileId, accessToken);
        
        if (metadataOpt.isEmpty()) {
            return Optional.empty();
        }

        FileMetadata metadata = metadataOpt.get();
        Path filePath = Paths.get(uploadDir, metadata.getStoredFilename());
        File file = filePath.toFile();
        
        return file.exists() ? Optional.of(file) : Optional.empty();
    }

    public List<FileMetadata> getFilesByAuthToken(String authToken) {
        return fileMetadataRepository.findByAuthTokenAndExpiresAtAfter(authToken, LocalDateTime.now());
    }

    public void cleanupExpiredFiles() {
        List<FileMetadata> expiredFiles = fileMetadataRepository.findExpiredFiles(LocalDateTime.now());
        
        for (FileMetadata metadata : expiredFiles) {
            try {
                Path filePath = Paths.get(uploadDir, metadata.getStoredFilename());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Error deleting file: " + metadata.getStoredFilename());
            }
        }
        
        fileMetadataRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}