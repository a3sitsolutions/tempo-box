package br.com.a3sitsolutions.tempo_box.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta do upload de arquivo")
public class FileUploadResponse {
    
    @Schema(description = "ID único do arquivo gerado pelo sistema", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String fileId;
    
    @Schema(description = "Token de acesso para download do arquivo", example = "custom-access-token-123")
    private String accessToken;
    
    @Schema(description = "Mensagem de confirmação", example = "File uploaded successfully")
    private String message;
    
    @Schema(description = "Tempo de expiração em minutos", example = "1440")
    private Long expiresInMinutes;
}