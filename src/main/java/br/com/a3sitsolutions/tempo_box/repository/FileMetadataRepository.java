package br.com.a3sitsolutions.tempo_box.repository;

import br.com.a3sitsolutions.tempo_box.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    
    Optional<FileMetadata> findByFileId(String fileId);
    
    Optional<FileMetadata> findByFileIdAndAccessToken(String fileId, String accessToken);
    
    List<FileMetadata> findByAuthTokenAndExpiresAtAfter(String authToken, LocalDateTime now);
    
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfter(String authToken, String idToken, LocalDateTime now);
    
    Optional<FileMetadata> findByFileIdAndAuthToken(String fileId, String authToken);
    
    Optional<FileMetadata> findByFileIdAndAuthTokenAndIdToken(String fileId, String authToken, String idToken);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.expiresAt < ?1")
    List<FileMetadata> findExpiredFiles(LocalDateTime now);
    
    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime expiredBefore);
}