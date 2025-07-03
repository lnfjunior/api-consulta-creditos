package br.com.api.creditos.controller;

import br.com.api.creditos.dto.CreditoResponseDTO;
import br.com.api.creditos.exception.CreditoNotFoundException;
import br.com.api.creditos.messaging.AuditoriaService;
import br.com.api.creditos.service.CreditoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes unitários para CreditoController.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CreditoController - Testes Unitários")
class CreditoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditoService creditoService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditoriaService auditoriaService;


    private CreditoResponseDTO creditoResponseMock;

    @BeforeEach
    void setUp() {
        creditoResponseMock = CreditoResponseDTO.builder()
                .numeroCredito("123456")
                .numeroNfse("7891011")
                .dataConstituicao(LocalDate.of(2024, 2, 25))
                .valorIssqn(new BigDecimal("1500.75"))
                .tipoCredito("ISSQN")
                .simplesNacional("Sim")
                .aliquota(new BigDecimal("5.0"))
                .valorFaturado(new BigDecimal("30000.00"))
                .valorDeducao(new BigDecimal("5000.00"))
                .baseCalculo(new BigDecimal("25000.00"))
                .build();
    }

    @Test
    @DisplayName("GET /api/creditos/{numeroNfse} - Deve retornar créditos por NFS-e")
    void deveRetornarCreditosPorNfse() throws Exception {
        // Given
        String numeroNfse = "7891011";
        List<CreditoResponseDTO> creditos = Arrays.asList(creditoResponseMock);
        when(creditoService.buscarCreditosPorNfse(numeroNfse)).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/{numeroNfse}", numeroNfse)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroCredito", is("123456")))
                .andExpect(jsonPath("$[0].numeroNfse", is("7891011")))
                .andExpect(jsonPath("$[0].tipoCredito", is("ISSQN")))
                .andExpect(jsonPath("$[0].simplesNacional", is("Sim")));
    }

    @Test
    @DisplayName("GET /api/creditos/{numeroNfse} - Deve retornar 404 quando NFS-e não encontrada")
    void deveRetornar404QuandoNfseNaoEncontrada() throws Exception {
        // Given
        String numeroNfse = "999999";
        when(creditoService.buscarCreditosPorNfse(numeroNfse))
                .thenThrow(CreditoNotFoundException.porNumeroNfse(numeroNfse));

        // When & Then
        mockMvc.perform(get("/api/creditos/{numeroNfse}", numeroNfse)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Nenhum crédito encontrado para a NFS-e: 999999")));
    }

    @Test
    @DisplayName("GET /api/creditos/credito/{numeroCredito} - Deve retornar crédito por número")
    void deveRetornarCreditoPorNumero() throws Exception {
        // Given
        String numeroCredito = "123456";
        when(creditoService.buscarCreditoPorNumero(numeroCredito)).thenReturn(creditoResponseMock);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}", numeroCredito)
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
    @DisplayName("GET /api/creditos/credito/{numeroCredito} - Deve retornar 404 quando crédito não encontrado")
    void deveRetornar404QuandoCreditoNaoEncontrado() throws Exception {
        // Given
        String numeroCredito = "999999";
        when(creditoService.buscarCreditoPorNumero(numeroCredito))
                .thenThrow(CreditoNotFoundException.porNumeroCredito(numeroCredito));

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}", numeroCredito)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Crédito não encontrado com o número: 999999")));
    }

    @Test
    @DisplayName("GET /api/creditos - Deve listar todos os créditos")
    void deveListarTodosCreditos() throws Exception {
        // Given
        List<CreditoResponseDTO> creditos = Arrays.asList(creditoResponseMock);
        when(creditoService.listarTodosCreditos()).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroCredito", is("123456")));
    }

    @Test
    @DisplayName("GET /api/creditos - Deve retornar lista vazia quando não há créditos")
    void deveRetornarListaVaziaQuandoNaoHaCreditos() throws Exception {
        // Given
        when(creditoService.listarTodosCreditos()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/creditos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/creditos/tipo/{tipo} - Deve retornar créditos por tipo")
    void deveRetornarCreditosPorTipo() throws Exception {
        // Given
        String tipo = "ISSQN";
        List<CreditoResponseDTO> creditos = Arrays.asList(creditoResponseMock);
        when(creditoService.buscarCreditosPorTipo(tipo)).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/tipo/{tipo}", tipo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tipoCredito", is("ISSQN")));
    }

    @Test
    @DisplayName("GET /api/creditos/recentes - Deve retornar créditos recentes com limite padrão")
    void deveRetornarCreditosRecentesComLimitePadrao() throws Exception {
        // Given
        List<CreditoResponseDTO> creditos = Arrays.asList(creditoResponseMock);
        when(creditoService.buscarUltimosCreditosConstituidos(10)).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/recentes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/creditos/recentes?limite=5 - Deve retornar créditos recentes com limite customizado")
    void deveRetornarCreditosRecentesComLimiteCustomizado() throws Exception {
        // Given
        int limite = 5;
        List<CreditoResponseDTO> creditos = Arrays.asList(creditoResponseMock);
        when(creditoService.buscarUltimosCreditosConstituidos(limite)).thenReturn(creditos);

        // When & Then
        mockMvc.perform(get("/api/creditos/recentes")
                        .param("limite", String.valueOf(limite))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/creditos/credito/{numeroCredito}/existe - Deve verificar existência de crédito")
    void deveVerificarExistenciaCredito() throws Exception {
        // Given
        String numeroCredito = "123456";
        when(creditoService.existeCreditoPorNumero(numeroCredito)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}/existe", numeroCredito)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/creditos/credito/{numeroCredito}/existe - Deve retornar false para crédito inexistente")
    void deveRetornarFalseParaCreditoInexistente() throws Exception {
        // Given
        String numeroCredito = "999999";
        when(creditoService.existeCreditoPorNumero(numeroCredito)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/creditos/credito/{numeroCredito}/existe", numeroCredito)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));
    }

    @Test
    @DisplayName("Deve retornar 400 para parâmetros inválidos")
    void deveRetornar400ParaParametrosInvalidos() throws Exception {
        // Given
        when(creditoService.buscarUltimosCreditosConstituidos(anyInt()))
                .thenThrow(new IllegalArgumentException("Limite deve ser maior que zero"));

        // When & Then
        mockMvc.perform(get("/api/creditos/recentes")
                        .param("limite", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }
}

