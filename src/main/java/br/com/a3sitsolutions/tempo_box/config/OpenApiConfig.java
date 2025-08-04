package br.com.a3sitsolutions.tempo_box.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Tempo Box API")
                        .version(appVersion)
                        .description("""
                                ## Sistema de Compartilhamento Temporário de Arquivos
                                
                                O **Tempo Box** é uma API REST para upload e compartilhamento temporário de arquivos com controle de expiração automática.
                                
                                ### Principais Funcionalidades:
                                - 📁 **Upload de arquivos** com duração personalizada
                                - 🔒 **Autenticação por token** estático
                                - 👤 **Filtragem por ID Token** para organização de arquivos por usuário
                                - ⏰ **Auto-expiração** baseada em tempo configurável
                                - 🗂️ **Metadados customizáveis** (repositório, commit, branch, etc.)
                                - 📊 **Suporte a arquivos** até 100MB
                                - 🔄 **Limpeza automática** de arquivos expirados
                                
                                ### Casos de Uso:
                                - Compartilhamento temporário de builds/artefatos
                                - Transferência segura de arquivos sensíveis
                                - Integração com pipelines CI/CD
                                - Distribuição temporária de documentos
                                - Cache de arquivos com TTL automático
                                
                                ### Autenticação:
                                Para usar a API, você precisa de um **Auth Token** válido. Opcionalmente, pode usar um **ID Token** para filtrar arquivos específicos do seu usuário.
                                
                                ### URLs de Acesso:
                                - **Produção**: https://tempo-box.a3sitsolutions.com.br
                                - **Swagger UI**: https://tempo-box.a3sitsolutions.com.br/swagger-ui.html
                                - **API Docs**: https://tempo-box.a3sitsolutions.com.br/v3/api-docs
                                """)
                        .contact(new Contact()
                                .name("A3S IT Solutions")
                                .email("contato@a3sitsolutions.com.br")
                                .url("https://a3sitsolutions.com.br"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("https://tempo-box.a3sitsolutions.com.br" + contextPath)
                                .description("Servidor de Produção"),
                        new Server()
                                .url("http://localhost:8080" + contextPath)
                                .description("Servidor de Desenvolvimento")
                ))
                .components(new Components()
                        .addSecuritySchemes("AuthToken", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-Auth-Token")
                                .description("Token de autenticação para acesso à API"))
                        .addSecuritySchemes("AccessToken", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("Access-Token")
                                .description("Token de acesso para download de arquivos específicos")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("AuthToken"));
    }
}