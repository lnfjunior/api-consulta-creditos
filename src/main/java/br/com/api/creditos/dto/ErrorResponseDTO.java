package br.com.api.creditos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respostas de erro padronizadas da API.
 * 
 * Este DTO é utilizado para retornar informações de erro
 * de forma consistente em toda a aplicação, incluindo
 * detalhes sobre o erro e timestamp da ocorrência.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta de erro padronizada da API")
public class ErrorResponseDTO {

    /**
     * Timestamp da ocorrência do erro.
     */
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora da ocorrência do erro", 
            example = "2024-02-25T10:30:00", 
            required = true)
    private LocalDateTime timestamp;

    /**
     * Código de status HTTP.
     */
    @JsonProperty("status")
    @Schema(description = "Código de status HTTP", 
            example = "404", 
            required = true)
    private Integer status;

    /**
     * Descrição do erro.
     */
    @JsonProperty("error")
    @Schema(description = "Descrição do tipo de erro", 
            example = "Not Found", 
            required = true)
    private String error;

    /**
     * Mensagem detalhada do erro.
     */
    @JsonProperty("message")
    @Schema(description = "Mensagem detalhada do erro", 
            example = "Crédito não encontrado com o número informado", 
            required = true)
    private String message;

    /**
     * Caminho da requisição que gerou o erro.
     */
    @JsonProperty("path")
    @Schema(description = "Caminho da requisição que gerou o erro", 
            example = "/api/creditos/credito/999999", 
            required = true)
    private String path;

    /**
     * Lista de erros de validação (quando aplicável).
     */
    @JsonProperty("validationErrors")
    @Schema(description = "Lista de erros de validação")
    private List<ValidationErrorDTO> validationErrors;

    /**
     * Cria uma resposta de erro simples.
     * 
     * @param status código de status HTTP
     * @param error descrição do erro
     * @param message mensagem detalhada
     * @param path caminho da requisição
     * @return ErrorResponseDTO configurado
     */
    public static ErrorResponseDTO of(Integer status, String error, String message, String path) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    /**
     * Cria uma resposta de erro com validações.
     * 
     * @param status código de status HTTP
     * @param error descrição do erro
     * @param message mensagem detalhada
     * @param path caminho da requisição
     * @param validationErrors lista de erros de validação
     * @return ErrorResponseDTO configurado
     */
    public static ErrorResponseDTO of(Integer status, String error, String message, String path, 
                                    List<ValidationErrorDTO> validationErrors) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .validationErrors(validationErrors)
                .build();
    }

    /**
     * DTO para erros de validação específicos.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "Erro de validação específico")
    public static class ValidationErrorDTO {

        /**
         * Campo que gerou o erro de validação.
         */
        @JsonProperty("field")
        @Schema(description = "Campo que gerou o erro", 
                example = "numeroCredito", 
                required = true)
        private String field;

        /**
         * Valor rejeitado.
         */
        @JsonProperty("rejectedValue")
        @Schema(description = "Valor que foi rejeitado", 
                example = "")
        private Object rejectedValue;

        /**
         * Mensagem de erro específica.
         */
        @JsonProperty("message")
        @Schema(description = "Mensagem de erro específica", 
                example = "Número do crédito é obrigatório", 
                required = true)
        private String message;

        /**
         * Cria um erro de validação.
         * 
         * @param field campo com erro
         * @param rejectedValue valor rejeitado
         * @param message mensagem de erro
         * @return ValidationErrorDTO configurado
         */
        public static ValidationErrorDTO of(String field, Object rejectedValue, String message) {
            return ValidationErrorDTO.builder()
                    .field(field)
                    .rejectedValue(rejectedValue)
                    .message(message)
                    .build();
        }
    }
}

