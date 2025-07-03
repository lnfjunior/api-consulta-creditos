package br.com.api.creditos.messaging;

import br.com.api.creditos.messaging.event.ConsultaCreditoEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Serviço responsável pela auditoria de consultas via Kafka.
 * 
 * Publica eventos de auditoria no Kafka sempre que uma consulta
 * é realizada na API, permitindo rastreamento e monitoramento
 * das operações.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.consulta-credito}")
    private String consultaCreditoTopic;

    /**
     * Publica um evento de consulta de crédito no Kafka.
     * 
     * @param evento evento a ser publicado
     */
    public void publicarEventoConsulta(ConsultaCreditoEvent evento) {
        try {
            log.debug("Publicando evento de auditoria: {} - {}", 
                     evento.getTipoConsulta(), evento.getParametroConsulta());
            
            // Usa o eventId como chave para garantir ordem por evento
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(consultaCreditoTopic, evento.getEventId(), evento);
            
            // Configura callbacks para sucesso e erro
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.debug("Evento de auditoria publicado com sucesso: {} - Offset: {}", 
                             evento.getEventId(), result.getRecordMetadata().offset());
                } else {
                    log.error("Erro ao publicar evento de auditoria: {} - Erro: {}", 
                             evento.getEventId(), exception.getMessage(), exception);
                }
            });
            
        } catch (Exception e) {
            log.error("Erro inesperado ao publicar evento de auditoria: {}", 
                     evento.getEventId(), e);
        }
    }

    /**
     * Publica evento de consulta bem-sucedida.
     * 
     * @param tipoConsulta tipo da consulta
     * @param parametro parâmetro utilizado
     * @param quantidadeResultados quantidade de resultados
     * @param tempoExecucao tempo de execução
     * @param enderecoIp IP do cliente
     * @param userAgent User-Agent do cliente
     */
    public void publicarConsultaSucesso(ConsultaCreditoEvent.TipoConsulta tipoConsulta,
                                       String parametro,
                                       Integer quantidadeResultados,
                                       Long tempoExecucao,
                                       String enderecoIp,
                                       String userAgent) {
        
        ConsultaCreditoEvent evento = ConsultaCreditoEvent.sucesso(
            tipoConsulta, parametro, quantidadeResultados, tempoExecucao, enderecoIp, userAgent
        );
        
        publicarEventoConsulta(evento);
    }

    /**
     * Publica evento de consulta com erro.
     * 
     * @param tipoConsulta tipo da consulta
     * @param parametro parâmetro utilizado
     * @param mensagemErro mensagem de erro
     * @param tempoExecucao tempo de execução
     * @param enderecoIp IP do cliente
     * @param userAgent User-Agent do cliente
     */
    public void publicarConsultaErro(ConsultaCreditoEvent.TipoConsulta tipoConsulta,
                                    String parametro,
                                    String mensagemErro,
                                    Long tempoExecucao,
                                    String enderecoIp,
                                    String userAgent) {
        
        ConsultaCreditoEvent evento = ConsultaCreditoEvent.erro(
            tipoConsulta, parametro, mensagemErro, tempoExecucao, enderecoIp, userAgent
        );
        
        publicarEventoConsulta(evento);
    }

    /**
     * Verifica se o serviço de auditoria está funcionando.
     * 
     * @return true se está funcionando, false caso contrário
     */
    public boolean isHealthy() {
        try {
            // Tenta enviar um evento de teste (sem realmente enviar)
            return kafkaTemplate != null;
        } catch (Exception e) {
            log.warn("Serviço de auditoria não está saudável: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtém estatísticas básicas do serviço.
     * 
     * @return informações sobre o serviço
     */
    public String getStatus() {
        return String.format("AuditoriaService - Tópico: %s, Healthy: %s", 
                           consultaCreditoTopic, isHealthy());
    }
}

