package br.com.api.creditos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller para endpoints de saúde e informações da aplicação.
 * 
 * Fornece endpoints para verificação do status da aplicação
 * e informações básicas sobre a versão e configuração.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@RestController
@RequestMapping("/api")
@Slf4j
@Tag(name = "Health", description = "Endpoints para verificação de saúde da aplicação")
public class HealthController {

    @Value("${spring.application.name:api-consulta-creditos}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String applicationVersion;

    /**
     * Endpoint de health check básico.
     * 
     * @return status da aplicação
     */
    @GetMapping("/health")
    @Operation(
        summary = "Health Check",
        description = "Verifica se a aplicação está funcionando corretamente"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Aplicação funcionando corretamente",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class)
        )
    )
    public ResponseEntity<Map<String, Object>> health() {
        log.debug("Health check solicitado");
        
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("application", applicationName);
        health.put("version", applicationVersion);
        
        return ResponseEntity.ok(health);
    }

    /**
     * Endpoint com informações detalhadas da aplicação.
     * 
     * @return informações da aplicação
     */
    @GetMapping("/info")
    @Operation(
        summary = "Informações da Aplicação",
        description = "Retorna informações detalhadas sobre a aplicação"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Informações retornadas com sucesso",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = Map.class)
        )
    )
    public ResponseEntity<Map<String, Object>> info() {
        log.debug("Informações da aplicação solicitadas");
        
        Map<String, Object> info = new HashMap<>();
        info.put("application", Map.of(
            "name", applicationName,
            "version", applicationVersion,
            "description", "API RESTful para consulta de créditos constituídos"
        ));
        
        info.put("build", Map.of(
            "java", System.getProperty("java.version"),
            "timestamp", LocalDateTime.now()
        ));
        
        info.put("endpoints", Map.of(
            "creditos_por_nfse", "/api/creditos/{numeroNfse}",
            "credito_por_numero", "/api/creditos/credito/{numeroCredito}",
            "todos_creditos", "/api/creditos",
            "creditos_por_tipo", "/api/creditos/tipo/{tipo}",
            "creditos_recentes", "/api/creditos/recentes",
            "verificar_existencia", "/api/creditos/credito/{numeroCredito}/existe"
        ));
        
        return ResponseEntity.ok(info);
    }

    /**
     * Endpoint para verificar conectividade básica.
     * 
     * @return resposta simples
     */
    @GetMapping("/ping")
    @Operation(
        summary = "Ping",
        description = "Endpoint simples para verificar conectividade"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Pong retornado com sucesso",
        content = @Content(
            mediaType = "text/plain",
            schema = @Schema(implementation = String.class)
        )
    )
    public ResponseEntity<String> ping() {
        log.debug("Ping solicitado");
        return ResponseEntity.ok("pong");
    }
}

