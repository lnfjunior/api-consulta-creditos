package br.com.api.creditos.controller;

import br.com.api.creditos.dto.CreditoResponseDTO;
import br.com.api.creditos.dto.ErrorResponseDTO;
import br.com.api.creditos.service.CreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações relacionadas a créditos constituídos.
 * 
 * Fornece endpoints para consulta de créditos por número da NFS-e
 * ou por número do crédito, conforme especificado no desafio técnico.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/creditos")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Créditos", description = "API para consulta de créditos constituídos")
public class CreditoController {

    private final CreditoService creditoService;

    /**
     * Busca créditos pelo número da NFS-e.
     * 
     * @param numeroNfse número da NFS-e
     * @return lista de créditos encontrados
     */
    @GetMapping("/{numeroNfse}")
    @Operation(
        summary = "Buscar créditos por NFS-e",
        description = "Retorna uma lista de créditos constituídos com base no número da NFS-e"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Créditos encontrados com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CreditoResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Nenhum crédito encontrado para a NFS-e informada",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetro inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<List<CreditoResponseDTO>> buscarCreditosPorNfse(
            @Parameter(description = "Número da NFS-e", example = "7891011", required = true)
            @PathVariable 
            @NotBlank(message = "Número da NFS-e é obrigatório") 
            String numeroNfse) {
        
        log.info("Recebida requisição para buscar créditos por NFS-e: {}", numeroNfse);
        
        List<CreditoResponseDTO> creditos = creditoService.buscarCreditosPorNfse(numeroNfse);
        
        log.info("Retornando {} crédito(s) para NFS-e: {}", creditos.size(), numeroNfse);
        return ResponseEntity.ok(creditos);
    }

    /**
     * Busca um crédito específico pelo número do crédito.
     * 
     * @param numeroCredito número do crédito
     * @return dados do crédito encontrado
     */
    @GetMapping("/credito/{numeroCredito}")
    @Operation(
        summary = "Buscar crédito por número",
        description = "Retorna os detalhes de um crédito constituído específico com base no número do crédito"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Crédito encontrado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreditoResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Crédito não encontrado com o número informado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetro inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<CreditoResponseDTO> buscarCreditoPorNumero(
            @Parameter(description = "Número do crédito", example = "123456", required = true)
            @PathVariable 
            @NotBlank(message = "Número do crédito é obrigatório") 
            String numeroCredito) {
        
        log.info("Recebida requisição para buscar crédito por número: {}", numeroCredito);
        
        CreditoResponseDTO credito = creditoService.buscarCreditoPorNumero(numeroCredito);
        
        log.info("Retornando crédito: {} - NFS-e: {}", numeroCredito, credito.getNumeroNfse());
        return ResponseEntity.ok(credito);
    }

    /**
     * Lista todos os créditos cadastrados.
     * 
     * @return lista de todos os créditos
     */
    @GetMapping
    @Operation(
        summary = "Listar todos os créditos",
        description = "Retorna uma lista com todos os créditos constituídos cadastrados no sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de créditos retornada com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CreditoResponseDTO.class))
            )
        )
    })
    public ResponseEntity<List<CreditoResponseDTO>> listarTodosCreditos() {
        log.info("Recebida requisição para listar todos os créditos");
        
        List<CreditoResponseDTO> creditos = creditoService.listarTodosCreditos();
        
        log.info("Retornando {} crédito(s)", creditos.size());
        return ResponseEntity.ok(creditos);
    }

    /**
     * Busca créditos por tipo.
     * 
     * @param tipo tipo do crédito
     * @return lista de créditos do tipo especificado
     */
    @GetMapping("/tipo/{tipo}")
    @Operation(
        summary = "Buscar créditos por tipo",
        description = "Retorna uma lista de créditos constituídos filtrados por tipo"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Créditos encontrados com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CreditoResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetro inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<List<CreditoResponseDTO>> buscarCreditosPorTipo(
            @Parameter(description = "Tipo do crédito", example = "ISSQN", required = true)
            @PathVariable 
            @NotBlank(message = "Tipo do crédito é obrigatório") 
            String tipo) {
        
        log.info("Recebida requisição para buscar créditos por tipo: {}", tipo);
        
        List<CreditoResponseDTO> creditos = creditoService.buscarCreditosPorTipo(tipo);
        
        log.info("Retornando {} crédito(s) do tipo: {}", creditos.size(), tipo);
        return ResponseEntity.ok(creditos);
    }

    /**
     * Busca os últimos créditos constituídos.
     * 
     * @param limite quantidade máxima de registros (padrão: 10)
     * @return lista dos últimos créditos
     */
    @GetMapping("/recentes")
    @Operation(
        summary = "Buscar créditos recentes",
        description = "Retorna uma lista dos últimos créditos constituídos, ordenados por data de constituição"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Créditos recentes encontrados com sucesso",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = CreditoResponseDTO.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetro inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<List<CreditoResponseDTO>> buscarCreditosRecentes(
            @Parameter(description = "Quantidade máxima de registros", example = "10")
            @RequestParam(defaultValue = "10") 
            int limite) {
        
        log.info("Recebida requisição para buscar {} créditos recentes", limite);
        
        List<CreditoResponseDTO> creditos = creditoService.buscarUltimosCreditosConstituidos(limite);
        
        log.info("Retornando {} crédito(s) recentes", creditos.size());
        return ResponseEntity.ok(creditos);
    }

    /**
     * Verifica se um crédito existe pelo número.
     * 
     * @param numeroCredito número do crédito
     * @return status da verificação
     */
    @GetMapping("/credito/{numeroCredito}/existe")
    @Operation(
        summary = "Verificar existência de crédito",
        description = "Verifica se existe um crédito com o número informado"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Verificação realizada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Boolean.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Parâmetro inválido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponseDTO.class)
            )
        )
    })
    public ResponseEntity<Boolean> verificarExistenciaCredito(
            @Parameter(description = "Número do crédito", example = "123456", required = true)
            @PathVariable 
            @NotBlank(message = "Número do crédito é obrigatório") 
            String numeroCredito) {
        
        log.info("Recebida requisição para verificar existência do crédito: {}", numeroCredito);
        
        boolean existe = creditoService.existeCreditoPorNumero(numeroCredito);
        
        log.info("Crédito {} {}", numeroCredito, existe ? "existe" : "não existe");
        return ResponseEntity.ok(existe);
    }
}

