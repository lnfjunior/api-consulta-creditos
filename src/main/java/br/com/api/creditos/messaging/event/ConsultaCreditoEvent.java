package br.com.api.creditos.messaging.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Evento para auditoria de consultas de créditos.
 * 
 * Este evento é publicado no Kafka sempre que uma consulta
 * é realizada na API, permitindo auditoria e monitoramento
 * das operações realizadas.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaCreditoEvent {

    /**
     * Identificador único do evento.
     */
    @JsonProperty("eventId")
    private String eventId;

    /**
     * Timestamp da consulta.
     */
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    /**
     * Tipo de consulta realizada.
     */
    @JsonProperty("tipoConsulta")
    private TipoConsulta tipoConsulta;

    /**
     * Parâmetro utilizado na consulta.
     */
    @JsonProperty("parametroConsulta")
    private String parametroConsulta;

    /**
     * Quantidade de resultados encontrados.
     */
    @JsonProperty("quantidadeResultados")
    private Integer quantidadeResultados;

    /**
     * Endereço IP do cliente que fez a consulta.
     */
    @JsonProperty("enderecoIp")
    private String enderecoIp;

    /**
     * User-Agent do cliente.
     */
    @JsonProperty("userAgent")
    private String userAgent;

    /**
     * Tempo de execução da consulta em milissegundos.
     */
    @JsonProperty("tempoExecucaoMs")
    private Long tempoExecucaoMs;

    /**
     * Indica se a consulta foi bem-sucedida.
     */
    @JsonProperty("sucesso")
    private Boolean sucesso;

    /**
     * Mensagem de erro (se houver).
     */
    @JsonProperty("mensagemErro")
    private String mensagemErro;

    /**
     * Informações adicionais sobre a consulta.
     */
    @JsonProperty("informacoesAdicionais")
    private String informacoesAdicionais;

    /**
     * Enumeration para tipos de consulta.
     */
    public enum TipoConsulta {
        @JsonProperty("CONSULTA_POR_NFSE")
        CONSULTA_POR_NFSE("Consulta por número da NFS-e"),
        
        @JsonProperty("CONSULTA_POR_NUMERO_CREDITO")
        CONSULTA_POR_NUMERO_CREDITO("Consulta por número do crédito"),
        
        @JsonProperty("LISTAR_TODOS")
        LISTAR_TODOS("Listagem de todos os créditos"),
        
        @JsonProperty("CONSULTA_POR_TIPO")
        CONSULTA_POR_TIPO("Consulta por tipo de crédito"),
        
        @JsonProperty("CONSULTA_RECENTES")
        CONSULTA_RECENTES("Consulta de créditos recentes"),
        
        @JsonProperty("VERIFICAR_EXISTENCIA")
        VERIFICAR_EXISTENCIA("Verificação de existência de crédito");

        private final String descricao;

        TipoConsulta(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    /**
     * Cria um evento de consulta bem-sucedida.
     * 
     * @param tipoConsulta tipo da consulta
     * @param parametro parâmetro utilizado
     * @param quantidadeResultados quantidade de resultados
     * @param tempoExecucao tempo de execução
     * @param enderecoIp IP do cliente
     * @param userAgent User-Agent do cliente
     * @return evento configurado
     */
    public static ConsultaCreditoEvent sucesso(TipoConsulta tipoConsulta, String parametro, 
                                             Integer quantidadeResultados, Long tempoExecucao,
                                             String enderecoIp, String userAgent) {
        return ConsultaCreditoEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .tipoConsulta(tipoConsulta)
                .parametroConsulta(parametro)
                .quantidadeResultados(quantidadeResultados)
                .enderecoIp(enderecoIp)
                .userAgent(userAgent)
                .tempoExecucaoMs(tempoExecucao)
                .sucesso(true)
                .build();
    }

    /**
     * Cria um evento de consulta com erro.
     * 
     * @param tipoConsulta tipo da consulta
     * @param parametro parâmetro utilizado
     * @param mensagemErro mensagem de erro
     * @param tempoExecucao tempo de execução
     * @param enderecoIp IP do cliente
     * @param userAgent User-Agent do cliente
     * @return evento configurado
     */
    public static ConsultaCreditoEvent erro(TipoConsulta tipoConsulta, String parametro, 
                                          String mensagemErro, Long tempoExecucao,
                                          String enderecoIp, String userAgent) {
        return ConsultaCreditoEvent.builder()
                .eventId(java.util.UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .tipoConsulta(tipoConsulta)
                .parametroConsulta(parametro)
                .quantidadeResultados(0)
                .enderecoIp(enderecoIp)
                .userAgent(userAgent)
                .tempoExecucaoMs(tempoExecucao)
                .sucesso(false)
                .mensagemErro(mensagemErro)
                .build();
    }
}

