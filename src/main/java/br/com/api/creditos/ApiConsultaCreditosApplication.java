package br.com.api.creditos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Classe principal da aplicação API de Consulta de Créditos.
 * 
 * Esta aplicação fornece endpoints RESTful para consulta de créditos constituídos,
 * incluindo funcionalidades de busca por número da NFS-e ou número do crédito.
 * 
 * Funcionalidades principais:
 * - Consulta de créditos por número da NFS-e
 * - Consulta de crédito específico por número do crédito
 * - Notificações via Kafka para auditoria de consultas
 * - Documentação automática via OpenAPI/Swagger
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@SpringBootApplication
@EnableKafka
public class ApiConsultaCreditosApplication {

    /**
     * Método principal para inicialização da aplicação.
     * 
     * @param args argumentos da linha de comando
     */
    public static void main(String[] args) {
        SpringApplication.run(ApiConsultaCreditosApplication.class, args);
    }
}

