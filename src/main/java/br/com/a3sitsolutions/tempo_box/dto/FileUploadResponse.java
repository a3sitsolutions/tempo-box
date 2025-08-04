package br.com.a3sitsolutions.tempo_box.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String fileId;
    private String accessToken;
    private String message;
    private Long expiresInMinutes;
}