package br.com.api.creditos.exception;

import br.com.api.creditos.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler global para tratamento de exceções da aplicação.
 * 
 * Centraliza o tratamento de exceções e padroniza as respostas de erro
 * retornadas pela API, garantindo consistência e melhor experiência
 * para os consumidores da API.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Trata exceções de crédito não encontrado.
     * 
     * @param ex exceção
     * @param request requisição HTTP
     * @return resposta de erro 404
     */
    @ExceptionHandler(CreditoNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCreditoNotFoundException(
            CreditoNotFoundException ex, HttpServletRequest request) {
        
        log.warn("Crédito não encontrado: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.of(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceções de validação de argumentos de método.
     * 
     * @param ex exceção
     * @param request requisição HTTP
     * @return resposta de erro 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        log.warn("Erro de validação: {}", ex.getMessage());
        
        List<ErrorResponseDTO.ValidationErrorDTO> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());
        
        ErrorResponseDTO error = ErrorResponseDTO.of(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Erro de validação nos dados fornecidos",
            request.getRequestURI(),
            validationErrors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de violação de constraints.
     * 
     * @param ex exceção
     * @param request requisição HTTP
     * @return resposta de erro 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        log.warn("Erro de constraint: {}", ex.getMessage());
        
        List<ErrorResponseDTO.ValidationErrorDTO> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(this::mapConstraintViolation)
                .collect(Collectors.toList());
        
        ErrorResponseDTO error = ErrorResponseDTO.of(
            HttpStatus.BAD_REQUEST.value(),
            "Constraint Violation",
            "Erro de validação de constraints",
            request.getRequestURI(),
            validationErrors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de tipo de argumento inválido.
     * 
     * @param ex exceção
     * @param request requisição HTTP
     * @return resposta de erro 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        log.warn("Erro de tipo de argumento: {}", ex.getMessage());
        
        String message = String.format(
            "Parâmetro '%s' deve ser do tipo %s", 
            ex.getName(), 
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido"
        );
        
        ErrorResponseDTO error = ErrorResponseDTO.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de mensagem HTTP não legível.
     * 
     * @param ex exceção
     * @param request requisição HTTP
     * @return resposta de erro 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        log.warn("Erro de leitura de mensagem HTTP: {}", ex.getMessage());
        
        ErrorResponseDTO error = ErrorResponseDTO.of(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Formato de dados inválido na requisição",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções genéricas não capturadas por outros handlers.
     * 
     * @param ex exceção
     * @param request requisição HTTP
     * @return resposta de erro 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Erro interno do servidor: ", ex);
        
        ErrorResponseDTO error = ErrorResponseDTO.of(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Erro interno do servidor. Tente novamente mais tarde.",
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Trata exceções do tipo IllegalArgumentException lançadas por validações manuais no controller.
     *
     * @param ex a exceção lançada
     * @return resposta HTTP 400 (Bad Request) com detalhes do erro
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", ex.getMessage()
                ));
    }

    /**
     * Mapeia FieldError para ValidationErrorDTO.
     * 
     * @param fieldError erro de campo
     * @return DTO de erro de validação
     */
    private ErrorResponseDTO.ValidationErrorDTO mapFieldError(FieldError fieldError) {
        return ErrorResponseDTO.ValidationErrorDTO.of(
            fieldError.getField(),
            fieldError.getRejectedValue(),
            fieldError.getDefaultMessage()
        );
    }

    /**
     * Mapeia ConstraintViolation para ValidationErrorDTO.
     * 
     * @param violation violação de constraint
     * @return DTO de erro de validação
     */
    private ErrorResponseDTO.ValidationErrorDTO mapConstraintViolation(ConstraintViolation<?> violation) {
        return ErrorResponseDTO.ValidationErrorDTO.of(
            violation.getPropertyPath().toString(),
            violation.getInvalidValue(),
            violation.getMessage()
        );
    }
}

