package br.com.a3sitsolutions.tempo_box.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health Check", description = "Endpoints para verificação de saúde da aplicação")
public class HealthController {

    @Operation(
            summary = "Health check simples",
            description = "Verifica se a aplicação está funcionando corretamente. Ideal para load balancers e monitoring."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aplicação funcionando corretamente")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "tempo-box");
        return ResponseEntity.ok(health);
    }

    @Operation(
            summary = "Health check com detalhes",
            description = "Verifica se a aplicação está funcionando com informações adicionais."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aplicação funcionando corretamente")
    })
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> healthDetailed() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "tempo-box");
        health.put("version", System.getProperty("app.version", "1.0.0"));
        
        // Verificações básicas
        Map<String, Object> checks = new HashMap<>();
        checks.put("memory", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        checks.put("uptime", System.currentTimeMillis());
        
        health.put("checks", checks);
        return ResponseEntity.ok(health);
    }
}