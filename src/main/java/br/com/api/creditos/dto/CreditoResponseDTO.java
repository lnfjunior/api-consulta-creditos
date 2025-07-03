package br.com.api.creditos.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para resposta de consulta de créditos.
 * 
 * Este DTO é utilizado para transferir dados de créditos constituídos
 * nas respostas das APIs, seguindo exatamente o formato especificado
 * no documento do desafio técnico.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados de um crédito constituído")
public class CreditoResponseDTO {

    /**
     * Número identificador único do crédito constituído.
     */
    @JsonProperty("numeroCredito")
    @Schema(description = "Número identificador único do crédito constituído", 
            example = "123456", 
            required = true)
    private String numeroCredito;

    /**
     * Número da Nota Fiscal de Serviços Eletrônica.
     */
    @JsonProperty("numeroNfse")
    @Schema(description = "Número da Nota Fiscal de Serviços Eletrônica", 
            example = "7891011", 
            required = true)
    private String numeroNfse;

    /**
     * Data de constituição do crédito.
     */
    @JsonProperty("dataConstituicao")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Data de constituição do crédito", 
            example = "2024-02-25", 
            required = true,
            type = "string",
            format = "date")
    private LocalDate dataConstituicao;

    /**
     * Valor do Imposto Sobre Serviços de Qualquer Natureza.
     */
    @JsonProperty("valorIssqn")
    @Schema(description = "Valor do Imposto Sobre Serviços de Qualquer Natureza", 
            example = "1500.75", 
            required = true)
    private BigDecimal valorIssqn;

    /**
     * Tipo do crédito.
     */
    @JsonProperty("tipoCredito")
    @Schema(description = "Tipo do crédito", 
            example = "ISSQN", 
            required = true,
            allowableValues = {"ISSQN", "Outros"})
    private String tipoCredito;

    /**
     * Indicação se é optante pelo Simples Nacional.
     */
    @JsonProperty("simplesNacional")
    @Schema(description = "Indicação se é optante pelo Simples Nacional", 
            example = "Sim", 
            required = true,
            allowableValues = {"Sim", "Não"})
    private String simplesNacional;

    /**
     * Alíquota aplicada no cálculo do imposto.
     */
    @JsonProperty("aliquota")
    @Schema(description = "Alíquota aplicada no cálculo do imposto (%)", 
            example = "5.0", 
            required = true)
    private BigDecimal aliquota;

    /**
     * Valor total faturado.
     */
    @JsonProperty("valorFaturado")
    @Schema(description = "Valor total faturado", 
            example = "30000.00", 
            required = true)
    private BigDecimal valorFaturado;

    /**
     * Valor das deduções aplicadas.
     */
    @JsonProperty("valorDeducao")
    @Schema(description = "Valor das deduções aplicadas", 
            example = "5000.00", 
            required = true)
    private BigDecimal valorDeducao;

    /**
     * Base de cálculo para o imposto.
     */
    @JsonProperty("baseCalculo")
    @Schema(description = "Base de cálculo para o imposto", 
            example = "25000.00", 
            required = true)
    private BigDecimal baseCalculo;

    /**
     * Converte o valor booleano do Simples Nacional para string.
     * 
     * @param simplesNacional valor booleano
     * @return "Sim" se true, "Não" se false
     */
    public static String formatSimplesNacional(Boolean simplesNacional) {
        return Boolean.TRUE.equals(simplesNacional) ? "Sim" : "Não";
    }

    /**
     * Converte a string do Simples Nacional para valor booleano.
     * 
     * @param simplesNacional string ("Sim" ou "Não")
     * @return true se "Sim", false caso contrário
     */
    public static Boolean parseSimplesNacional(String simplesNacional) {
        return "Sim".equalsIgnoreCase(simplesNacional);
    }
}

