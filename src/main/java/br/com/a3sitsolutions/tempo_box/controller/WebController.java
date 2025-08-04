package br.com.a3sitsolutions.tempo_box.controller;

import br.com.a3sitsolutions.tempo_box.entity.FileMetadata;
import br.com.a3sitsolutions.tempo_box.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Tag(name = "Web Interface", description = "Endpoints para a interface web do Tempo Box")
public class WebController {

    private final FileStorageService fileStorageService;

    @Value("${app.auth.static-token}")
    private String staticAuthToken;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                          @RequestParam(value = "logout", required = false) String logout,
                          Model model) {
        if (error != null) {
            model.addAttribute("error", true);
        }
        if (logout != null) {
            model.addAttribute("logout", true);
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("token") String token, 
                       @RequestParam(value = "idToken", required = false) String idToken,
                       HttpSession session, Model model) {
        System.out.println("Login - Session ID: " + session.getId());
        System.out.println("Login - Received token: '" + token + "'");
        System.out.println("Login - Received idToken: '" + idToken + "'");
        
        if (staticAuthToken.equals(token)) {
            session.setAttribute("authToken", token);
            if (idToken != null && !idToken.trim().isEmpty()) {
                session.setAttribute("idToken", idToken);
                System.out.println("Login - idToken stored in session: " + idToken);
            }
            System.out.println("Login - Token stored in session, redirecting to /files");
            return "redirect:/files";
        } else {
            System.out.println("Login - Invalid token, showing error");
            model.addAttribute("error", true);
            return "login";
        }
    }

    @GetMapping("/files")
    public String filesPage(HttpSession session, Model model) {
        String authToken = (String) session.getAttribute("authToken");
        String idToken = (String) session.getAttribute("idToken");
        System.out.println("Files page access - Session ID: " + session.getId());
        System.out.println("Files page access - Auth token from session: '" + authToken + "'");
        System.out.println("Files page access - ID token from session: '" + idToken + "'");
        System.out.println("Files page access - Expected token: '" + staticAuthToken + "'");
        
        if (authToken == null || !staticAuthToken.equals(authToken)) {
            System.out.println("Files page access - Authentication failed, redirecting to login");
            return "redirect:/login";
        }

        System.out.println("Files page access - Authentication successful, loading files");
        List<FileMetadata> files = fileStorageService.getFilesByAuthTokenAndIdToken(authToken, idToken);
        model.addAttribute("files", files);
        model.addAttribute("idToken", idToken); // Para exibir na interface se necessário
        return "files";
    }

    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<?> downloadFileWeb(@PathVariable String fileId,
                                           @RequestParam("token") String accessToken) {
        
        Optional<FileMetadata> metadataOpt = fileStorageService.getFileMetadata(fileId, accessToken);
        
        if (metadataOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        FileMetadata metadata = metadataOpt.get();
        
        if (metadata.isExpired()) {
            return ResponseEntity.notFound().build();
        }

        Optional<File> fileOpt = fileStorageService.getFile(fileId, accessToken);
        
        if (fileOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @PostMapping("/files/expire/{fileId}")
    public String expireFile(@PathVariable String fileId, HttpSession session) {
        String authToken = (String) session.getAttribute("authToken");
        
        if (authToken == null || !staticAuthToken.equals(authToken)) {
            return "redirect:/login";
        }
        
        fileStorageService.expireFile(fileId, authToken);
        return "redirect:/files";
    }

    @Operation(
            summary = "Login automático com Auth Token",
            description = "Realiza login automático usando apenas o Auth Token via URL"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirecionamento para área de arquivos ou login com erro")
    })
    @GetMapping("/auth/{authToken}")
    public String autoAuthenticate(
            @Parameter(description = "Token de autenticação", example = "tempo-box-admin-token-2024")
            @PathVariable String authToken, 
            HttpSession session) {
        if (staticAuthToken.equals(authToken)) {
            session.setAttribute("authToken", authToken);
            return "redirect:/files";
        } else {
            return "redirect:/login?error";
        }
    }

    @Operation(
            summary = "Login automático com Auth Token e ID Token",
            description = """
                    Realiza login automático usando Auth Token e ID Token via URL.
                    
                    ### Funcionalidade:
                    - Autentica o usuário com Auth Token
                    - Define ID Token para filtrar arquivos específicos
                    - Redireciona para área de arquivos
                    
                    ### Exemplo de URL:
                    ```
                    https://tempo-box.a3sitsolutions.com.br/auth/tempo-box-admin-token-2024/token-identification-xpto
                    ```
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirecionamento para área de arquivos ou login com erro")
    })
    @GetMapping("/auth/{authToken}/{idToken}")
    public String autoAuthenticateWithIdToken(
            @Parameter(description = "Token de autenticação", example = "tempo-box-admin-token-2024")
            @PathVariable String authToken, 
            @Parameter(description = "Token de identificação para filtrar arquivos", example = "token-identification-xpto")
            @PathVariable String idToken, 
            HttpSession session) {
        System.out.println("Auto-authenticate - Auth token: '" + authToken + "'");
        System.out.println("Auto-authenticate - ID token: '" + idToken + "'");
        
        if (staticAuthToken.equals(authToken)) {
            session.setAttribute("authToken", authToken);
            if (idToken != null && !idToken.trim().isEmpty() && !idToken.equals("null")) {
                session.setAttribute("idToken", idToken);
                System.out.println("Auto-authenticate - ID token stored in session: " + idToken);
            }
            System.out.println("Auto-authenticate - Redirecting to /files");
            return "redirect:/files";
        } else {
            System.out.println("Auto-authenticate - Invalid auth token, redirecting to login with error");
            return "redirect:/login?error";
        }
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
}