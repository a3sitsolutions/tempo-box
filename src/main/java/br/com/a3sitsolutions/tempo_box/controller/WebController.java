package br.com.a3sitsolutions.tempo_box.controller;

import br.com.a3sitsolutions.tempo_box.entity.FileMetadata;
import br.com.a3sitsolutions.tempo_box.service.FileStorageService;
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
    public String login(@RequestParam("token") String token, HttpSession session, Model model) {
        System.out.println("Login - Session ID: " + session.getId());
        System.out.println("Login - Received token: '" + token + "'");
        
        if (staticAuthToken.equals(token)) {
            session.setAttribute("authToken", token);
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
        System.out.println("Files page access - Session ID: " + session.getId());
        System.out.println("Files page access - Auth token from session: '" + authToken + "'");
        System.out.println("Files page access - Expected token: '" + staticAuthToken + "'");
        
        if (authToken == null || !staticAuthToken.equals(authToken)) {
            System.out.println("Files page access - Authentication failed, redirecting to login");
            return "redirect:/login";
        }

        System.out.println("Files page access - Authentication successful, loading files");
        List<FileMetadata> files = fileStorageService.getFilesByAuthToken(authToken);
        model.addAttribute("files", files);
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

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}