package br.com.api.creditos.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API.
 * 
 * Define as informações da API que serão exibidas na documentação
 * automática gerada pelo Swagger UI.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configura as informações da API para documentação.
     * 
     * @return configuração do OpenAPI
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Consulta de Créditos")
                        .description("""
                                API RESTful para consulta de créditos constituídos.
                                
                                Esta API fornece endpoints para consulta de créditos por número da NFS-e 
                                ou por número do crédito, incluindo informações como valor do ISSQN, 
                                tipo do crédito, alíquota e outros dados fiscais.
                                
                                **Funcionalidades principais:**
                                - Consulta de créditos por número da NFS-e
                                - Consulta de crédito específico por número
                                - Listagem de todos os créditos
                                - Filtros por tipo de crédito
                                - Consulta de créditos recentes
                                - Verificação de existência de crédito
                                
                                **Tecnologias utilizadas:**
                                - Java 21
                                - Spring Boot 3.2.0
                                - PostgreSQL
                                - Apache Kafka (para auditoria)
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Luiz Nogueira")
                                .email("lnfjunior@gmail.com")
                                .url("https://empresa.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desenvolvimento"),
                        new Server()
                                .url("https://api-creditos.empresa.com")
                                .description("Servidor de Produção")
                ));
    }
}

