package br.com.a3sitsolutions.tempo_box.controller;

import br.com.a3sitsolutions.tempo_box.dto.FileDescriptionDto;
import br.com.a3sitsolutions.tempo_box.dto.FileUploadResponse;
import br.com.a3sitsolutions.tempo_box.entity.FileMetadata;
import br.com.a3sitsolutions.tempo_box.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "File API", description = "API para upload e download de arquivos temporários")
public class FileApiController {

    private final FileStorageService fileStorageService;

    @Operation(
            summary = "Upload de arquivo temporário",
            description = """
                    Faz upload de um arquivo com tempo de vida configurável. O arquivo será automaticamente removido após o tempo especificado.
                    
                    ### Funcionalidades:
                    - Upload de arquivos até 100MB
                    - Controle de tempo de vida (em minutos)
                    - Token de identificação para filtragem de arquivos
                    - Metadados opcionais para integração CI/CD
                    - Token de acesso customizável para download
                    
                    ### Parâmetros obrigatórios:
                    - **file**: Arquivo a ser enviado
                    - **storageDurationMinutes**: Tempo de vida em minutos
                    - **authToken**: Token de autenticação
                    
                    ### Exemplo de chamada:
                    ```bash
                    curl -X POST 'https://tempo-box.a3sitsolutions.com.br/api/files/upload' \\
                      -F 'file=@"arquivo.zip"' \\
                      -F 'authToken="tempo-box-admin-token-2024"' \\
                      -F 'idToken="meu-token-unico"' \\
                      -F 'storageDurationMinutes="1440"' \\
                      -F 'repository="meu-projeto"' \\
                      -F 'branch="main"' \\
                      -F 'commit="abc123"'
                    ```
                    """,
            security = @SecurityRequirement(name = "AuthToken")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Upload realizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FileUploadResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Response",
                                    value = """
                                            {
                                              "fileId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
                                              "accessToken": "custom-access-token-123",
                                              "message": "File uploaded successfully",
                                              "storageDurationMinutes": 1440
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token de autenticação inválido",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unauthorized",
                                    value = """
                                            {
                                              "error": "Unauthorized",
                                              "message": "Invalid authentication token"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno do servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Upload Failed",
                                    value = """
                                            {
                                              "error": "Upload failed",
                                              "message": "IO exception occurred during file upload"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @Parameter(description = "Arquivo a ser enviado (máximo 100MB)", required = true)
            @RequestParam("file") MultipartFile file,
            
            @Parameter(description = "Tempo de vida do arquivo em minutos", required = true, example = "1440")
            @RequestParam("storageDurationMinutes") Integer storageDurationMinutes,
            
            @Parameter(description = "Token de autenticação", required = true, example = "tempo-box-admin-token-2024")
            @RequestParam("authToken") String authToken,
            
            @Parameter(description = "Token de identificação único para filtrar arquivos por usuário", example = "token-identification-xpto")
            @RequestParam(value = "idToken", required = false) String idToken,
            
            @Parameter(description = "Token de acesso personalizado para download (se não fornecido, será gerado automaticamente)", example = "custom-access-token-123")
            @RequestParam(value = "accessToken", required = false) String accessToken,
            
            @Parameter(description = "Nome do repositório Git (metadado)", example = "meu-projeto")
            @RequestParam(value = "repository", required = false) String repository,
            
            @Parameter(description = "Hash do commit Git (metadado)", example = "abc1234567890")
            @RequestParam(value = "commit", required = false) String commit,
            
            @Parameter(description = "Mensagem do commit Git (metadado)", example = "feat: adiciona nova funcionalidade")
            @RequestParam(value = "commitMessage", required = false) String commitMessage,
            
            @Parameter(description = "Nome da branch Git (metadado)", example = "main")
            @RequestParam(value = "branch", required = false) String branch,
            
            @Parameter(description = "ID da execução CI/CD (metadado)", example = "12345")
            @RequestParam(value = "runId", required = false) String runId,
            
            @Parameter(description = "Autor do commit/execução (metadado)", example = "developer@example.com")
            @RequestParam(value = "actor", required = false) String actor,
            
            @Parameter(description = "Nome do APK (para builds Android)", example = "app-release.apk")
            @RequestParam(value = "apkName", required = false) String apkName,
            
            @Parameter(description = "URL de download alternativa (metadado)", example = "https://example.com/download/file")
            @RequestParam(value = "downloadUrl", required = false) String downloadUrl) {

        try {
            FileDescriptionDto description = new FileDescriptionDto(
                    repository, commit, commitMessage, branch, runId, actor, apkName, downloadUrl
            );

            FileUploadResponse response = fileStorageService.uploadFile(
                    file, storageDurationMinutes, accessToken, authToken, idToken, description
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

    @Operation(
            summary = "Download de arquivo",
            description = """
                    Faz download de um arquivo específico utilizando o fileId e accessToken.
                    
                    ### Funcionamento:
                    - Verifica se o arquivo existe e não expirou
                    - Valida o token de acesso
                    - Retorna o arquivo para download
                    
                    ### Parâmetros:
                    - **fileId**: ID único do arquivo (obtido no upload)
                    - **Access-Token**: Token de acesso (header)
                    
                    ### Exemplo de chamada:
                    ```bash
                    curl -X GET 'https://tempo-box.a3sitsolutions.com.br/api/files/download/{fileId}' \\
                      -H 'Access-Token: custom-access-token-123' \\
                      -o arquivo_baixado.zip
                    ```
                    """,
            security = @SecurityRequirement(name = "AccessToken")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Download realizado com sucesso",
                    content = @Content(
                            mediaType = "application/octet-stream",
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token de acesso inválido ou arquivo não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Unauthorized",
                                    value = """
                                            {
                                              "error": "Unauthorized",
                                              "message": "Invalid file ID or access token"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "410",
                    description = "Arquivo expirado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "File Expired",
                                    value = """
                                            {
                                              "error": "File Expired",
                                              "message": "The requested file has expired and is no longer available",
                                              "expiredAt": "2024-01-15T10:30:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Arquivo não encontrado no servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "File Not Found",
                                    value = """
                                            {
                                              "error": "File Not Found",
                                              "message": "The requested file could not be found on the server"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/download/{fileId}")
    public ResponseEntity<?> downloadFile(
            @Parameter(description = "ID único do arquivo", required = true, example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String fileId,
            
            @Parameter(description = "Token de acesso para o arquivo", required = true, example = "custom-access-token-123")
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