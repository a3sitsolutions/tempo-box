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
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.createdAt DESC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByCreatedAtDesc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.createdAt DESC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByCreatedAtDesc(String authToken, String idToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.createdAt ASC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByCreatedAtAsc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.createdAt ASC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByCreatedAtAsc(String authToken, String idToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.expiresAt DESC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByExpiresAtDesc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.expiresAt DESC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByExpiresAtDesc(String authToken, String idToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.expiresAt ASC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByExpiresAtAsc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.expiresAt ASC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByExpiresAtAsc(String authToken, String idToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.originalFilename ASC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByFilenameAsc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.originalFilename ASC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByFilenameAsc(String authToken, String idToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.originalFilename DESC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByFilenameDesc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.originalFilename DESC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByFilenameDesc(String authToken, String idToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.fileSize ASC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByFileSizeAsc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.fileSize ASC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByFileSizeAsc(String authToken, String idToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.expiresAt > ?2 ORDER BY f.fileSize DESC")
    List<FileMetadata> findByAuthTokenAndExpiresAtAfterOrderByFileSizeDesc(String authToken, LocalDateTime now);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.authToken = ?1 AND f.idToken = ?2 AND f.expiresAt > ?3 ORDER BY f.fileSize DESC")
    List<FileMetadata> findByAuthTokenAndIdTokenAndExpiresAtAfterOrderByFileSizeDesc(String authToken, String idToken, LocalDateTime now);
    
    Optional<FileMetadata> findByFileIdAndAuthToken(String fileId, String authToken);
    
    Optional<FileMetadata> findByFileIdAndAuthTokenAndIdToken(String fileId, String authToken, String idToken);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.expiresAt < ?1")
    List<FileMetadata> findExpiredFiles(LocalDateTime now);
    
    @Modifying
    void deleteByExpiresAtBefore(LocalDateTime expiredBefore);
}