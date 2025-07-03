package br.com.api.creditos.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuração do Apache Kafka para a aplicação.
 * 
 * Define as configurações necessárias para produção de mensagens
 * no Kafka, incluindo serializers e configurações de tópicos.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Configuration
@Slf4j
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.consulta-credito}")
    private String consultaCreditoTopic;

    /**
     * Configura o factory para produtores Kafka.
     * 
     * @return factory configurado
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        
        // Configurações básicas
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Configurações de performance e confiabilidade
        configProps.put(ProducerConfig.ACKS_CONFIG, "1"); // Aguarda confirmação do líder
        configProps.put(ProducerConfig.RETRIES_CONFIG, 3); // Tentativas de reenvio
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // Tamanho do batch
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, 5); // Tempo de espera para batch
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // Buffer de memória
        
        // Configurações de timeout
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000);
        
        // Configurações de compressão
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        
        // Configurações específicas do JsonSerializer
        configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        
        log.info("Configurando Kafka Producer com bootstrap servers: {}", bootstrapServers);
        
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Configura o template Kafka para envio de mensagens.
     * 
     * @return template configurado
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        
        // Configurações adicionais do template
        template.setDefaultTopic(consultaCreditoTopic);
        
        log.info("KafkaTemplate configurado com tópico padrão: {}", consultaCreditoTopic);
        
        return template;
    }

    /**
     * Cria o tópico para eventos de consulta de crédito.
     * 
     * @return configuração do tópico
     */
    @Bean
    public NewTopic consultaCreditoTopic() {
        log.info("Criando tópico: {}", consultaCreditoTopic);
        
        return TopicBuilder.name(consultaCreditoTopic)
                .partitions(3) // 3 partições para paralelismo
                .replicas(1) // 1 réplica (ajustar conforme ambiente)
                .config("retention.ms", "604800000") // 7 dias de retenção
                .config("cleanup.policy", "delete") // Política de limpeza
                .config("compression.type", "snappy") // Compressão
                .build();
    }
}

