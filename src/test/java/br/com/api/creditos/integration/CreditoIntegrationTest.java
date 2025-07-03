package br.com.api.creditos.integration;

import br.com.api.creditos.entity.Credito;
import br.com.api.creditos.repository.CreditoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para a API de Consulta de Créditos.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "logging.level.br.com.api.creditos=WARN"
})
@Transactional
@DisplayName("Créditos API - Testes de Integração")
@EmbeddedKafka(partitions = 1, controlledShutdown = true, topics = "topico-auditoria")
class CreditoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CreditoRepository creditoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Credito creditoTeste;

    @BeforeEach
    void setUp() {
        // Limpa o banco de dados
        creditoRepository.deleteAll();

        // Cria dados de teste
        creditoTeste = Credito.builder()
                .numeroCredito("123456")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 25))
                .valorIssqn(new BigDecimal("1500.75"))
                .tipoCredito("ISSQN")
                .simplesNacional(true)
                .aliquota(new BigDecimal("5.0"))
                .valorFaturado(new BigDecimal("30000.00"))
                .valorDeducao(new BigDecimal("5000.00"))
                .baseCalculo(new BigDecimal("25000.00"))
                .build();

        creditoRepository.save(creditoTeste);

        // Cria um segundo crédito para a mesma NFS-e
        Credito creditoTeste2 = Credito.builder()
                .numeroCredito("789012")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 26))
                .valorIssqn(new BigDecimal("1200.50"))
                .tipoCredito("ISSQN")
                .simplesNacional(false)
                .aliquota(new BigDecimal("4.5"))
                .valorFaturado(new BigDecimal("25000.00"))
                .valorDeducao(new BigDecimal("4000.00"))
                .baseCalculo(new BigDecimal("21000.00"))
                .build();

        creditoRepository.save(creditoTeste2);
    }

    @Test
    @DisplayName("Integração: Deve buscar créditos por NFS-e com dados reais")
    void deveBuscarCreditosPorNfseComDadosReais() throws Exception {
        mockMvc.perform(get("/api/creditos/{numeroNfse}", "7891011")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].numeroNfse", is("7891011")))
                .andExpect(jsonPath("$[1].numeroNfse", is("7891011")))
                // Verifica ordenação por data (mais recente primeiro)
                .andExpect(jsonPath("$[0].dataConstituicao", is("2024-02-26")))
                .andExpect(jsonPath("$[1].dataConstituicao", is("2024-02-25")));
    }

    @Test
    @DisplayName("Integração: Deve buscar crédito específico por número")
    void deveBuscarCreditoEspecificoPorNumero() throws Exception {
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}", "123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroCredito", is("123456")))
                .andExpect(jsonPath("$.numeroNfse", is("7891011")))
                .andExpect(jsonPath("$.dataConstituicao", is("2024-02-25")))
                .andExpect(jsonPath("$.valorIssqn", is(1500.75)))
                .andExpect(jsonPath("$.tipoCredito", is("ISSQN")))
                .andExpect(jsonPath("$.simplesNacional", is("Sim")))
                .andExpect(jsonPath("$.aliquota", is(5.0)))
                .andExpect(jsonPath("$.valorFaturado", is(30000.00)))
                .andExpect(jsonPath("$.valorDeducao", is(5000.00)))
                .andExpect(jsonPath("$.baseCalculo", is(25000.00)));
    }

    @Test
    @DisplayName("Integração: Deve listar todos os créditos")
    void deveListarTodosCreditos() throws Exception {
        mockMvc.perform(get("/api/creditos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].numeroCredito", containsInAnyOrder("123456", "789012")));
    }

    @Test
    @DisplayName("Integração: Deve buscar créditos por tipo")
    void deveBuscarCreditosPorTipo() throws Exception {
        mockMvc.perform(get("/api/creditos/tipo/{tipo}", "ISSQN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].tipoCredito", everyItem(is("ISSQN"))));
    }

    @Test
    @DisplayName("Integração: Deve verificar existência de crédito")
    void deveVerificarExistenciaCredito() throws Exception {
        // Crédito existente
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}/existe", "123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        // Crédito inexistente
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}/existe", "999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("Integração: Deve buscar créditos recentes")
    void deveBuscarCreditosRecentes() throws Exception {
        mockMvc.perform(get("/api/creditos/recentes")
                        .param("limite", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                // Deve retornar o mais recente (26/02)
                .andExpect(jsonPath("$[0].dataConstituicao", is("2024-02-26")));
    }

    @Test
    @DisplayName("Integração: Deve retornar 404 para NFS-e inexistente")
    void deveRetornar404ParaNfseInexistente() throws Exception {
        mockMvc.perform(get("/api/creditos/{numeroNfse}", "999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Nenhum crédito encontrado para a NFS-e: 999999")))
                .andExpect(jsonPath("$.path", is("/api/creditos/999999")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    @Test
    @DisplayName("Integração: Deve retornar 404 para crédito inexistente")
    void deveRetornar404ParaCreditoInexistente() throws Exception {
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}", "999999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Crédito não encontrado com o número: 999999")))
                .andExpect(jsonPath("$.path", is("/api/creditos/credito/999999")));
    }

    @Test
    @DisplayName("Integração: Deve validar formato de resposta JSON")
    void deveValidarFormatoRespostaJson() throws Exception {
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}", "123456")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Verifica se todos os campos obrigatórios estão presentes
                .andExpect(jsonPath("$.numeroCredito", notNullValue()))
                .andExpect(jsonPath("$.numeroNfse", notNullValue()))
                .andExpect(jsonPath("$.dataConstituicao", notNullValue()))
                .andExpect(jsonPath("$.valorIssqn", notNullValue()))
                .andExpect(jsonPath("$.tipoCredito", notNullValue()))
                .andExpect(jsonPath("$.simplesNacional", notNullValue()))
                .andExpect(jsonPath("$.aliquota", notNullValue()))
                .andExpect(jsonPath("$.valorFaturado", notNullValue()))
                .andExpect(jsonPath("$.valorDeducao", notNullValue()))
                .andExpect(jsonPath("$.baseCalculo", notNullValue()))
                // Verifica tipos de dados
                .andExpect(jsonPath("$.valorIssqn", isA(Number.class)))
                .andExpect(jsonPath("$.aliquota", isA(Number.class)))
                .andExpect(jsonPath("$.valorFaturado", isA(Number.class)))
                .andExpect(jsonPath("$.valorDeducao", isA(Number.class)))
                .andExpect(jsonPath("$.baseCalculo", isA(Number.class)))
                // Verifica formato da data
                .andExpect(jsonPath("$.dataConstituicao", matchesPattern("\\d{4}-\\d{2}-\\d{2}")));
    }

    @Test
    @DisplayName("Integração: Deve testar CORS headers")
    void deveTestarCorsHeaders() throws Exception {
        mockMvc.perform(get("/api/creditos")
                        .header("Origin", "http://localhost:4200")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
    }
}

