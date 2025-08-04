package br.com.a3sitsolutions.tempo_box.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de erro da API")
public class ErrorResponse {
    
    @Schema(description = "Tipo do erro", example = "Unauthorized")
    private String error;
    
    @Schema(description = "Mensagem detalhada do erro", example = "Invalid authentication token")
    private String message;
    
    @Schema(description = "Data de expiração (quando aplicável)", example = "2024-01-15T10:30:00")
    private String expiredAt;
}