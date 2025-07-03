package br.com.api.creditos.service;

import br.com.api.creditos.dto.CreditoMapper;
import br.com.api.creditos.dto.CreditoResponseDTO;
import br.com.api.creditos.entity.Credito;
import br.com.api.creditos.exception.CreditoNotFoundException;
import br.com.api.creditos.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para CreditoServiceImpl.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreditoService - Testes Unitários")
class CreditoServiceTest {

    @Mock
    private CreditoRepository creditoRepository;

    @Mock
    private CreditoMapper creditoMapper;

    @InjectMocks
    private CreditoServiceImpl creditoService;

    private Credito creditoMock;
    private CreditoResponseDTO creditoResponseMock;

    @BeforeEach
    void setUp() {
        creditoMock = Credito.builder()
                .id(1L)
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
    @DisplayName("Deve buscar créditos por NFS-e com sucesso")
    void deveBuscarCreditosPorNfseComSucesso() {
        // Given
        String numeroNfse = "7891011";
        List<Credito> creditos = Arrays.asList(creditoMock);
        List<CreditoResponseDTO> creditosResponse = Arrays.asList(creditoResponseMock);

        when(creditoRepository.findByNumeroNfseOrderByDataConstituicaoDesc(numeroNfse))
                .thenReturn(creditos);
        when(creditoMapper.toResponseDTOList(creditos)).thenReturn(creditosResponse);

        // When
        List<CreditoResponseDTO> resultado = creditoService.buscarCreditosPorNfse(numeroNfse);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNumeroNfse()).isEqualTo(numeroNfse);

        verify(creditoRepository).findByNumeroNfseOrderByDataConstituicaoDesc(numeroNfse);
        verify(creditoMapper).toResponseDTOList(creditos);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não encontrar créditos por NFS-e")
    void deveLancarExcecaoQuandoNaoEncontrarCreditosPorNfse() {
        // Given
        String numeroNfse = "999999";
        when(creditoRepository.findByNumeroNfseOrderByDataConstituicaoDesc(numeroNfse))
                .thenReturn(Collections.emptyList());

        // When & Then
        assertThatThrownBy(() -> creditoService.buscarCreditosPorNfse(numeroNfse))
                .isInstanceOf(CreditoNotFoundException.class)
                .hasMessageContaining("Nenhum crédito encontrado para a NFS-e: 999999");

        verify(creditoRepository).findByNumeroNfseOrderByDataConstituicaoDesc(numeroNfse);
        verify(creditoMapper, never()).toResponseDTOList(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando NFS-e for vazia")
    void deveLancarExcecaoQuandoNfseForVazia() {
        // When & Then
        assertThatThrownBy(() -> creditoService.buscarCreditosPorNfse(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número da NFS-e não pode ser vazio ou nulo");

        verify(creditoRepository, never()).findByNumeroNfseOrderByDataConstituicaoDesc(anyString());
    }

    @Test
    @DisplayName("Deve buscar crédito por número com sucesso")
    void deveBuscarCreditoPorNumeroComSucesso() {
        // Given
        String numeroCredito = "123456";
        when(creditoRepository.findByNumeroCredito(numeroCredito))
                .thenReturn(Optional.of(creditoMock));
        when(creditoMapper.toResponseDTO(creditoMock)).thenReturn(creditoResponseMock);

        // When
        CreditoResponseDTO resultado = creditoService.buscarCreditoPorNumero(numeroCredito);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNumeroCredito()).isEqualTo(numeroCredito);

        verify(creditoRepository).findByNumeroCredito(numeroCredito);
        verify(creditoMapper).toResponseDTO(creditoMock);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não encontrar crédito por número")
    void deveLancarExcecaoQuandoNaoEncontrarCreditoPorNumero() {
        // Given
        String numeroCredito = "999999";
        when(creditoRepository.findByNumeroCredito(numeroCredito))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> creditoService.buscarCreditoPorNumero(numeroCredito))
                .isInstanceOf(CreditoNotFoundException.class)
                .hasMessageContaining("Crédito não encontrado com o número: 999999");

        verify(creditoRepository).findByNumeroCredito(numeroCredito);
        verify(creditoMapper, never()).toResponseDTO(any());
    }

    @Test
    @DisplayName("Deve listar todos os créditos com sucesso")
    void deveListarTodosCreditosComSucesso() {
        // Given
        List<Credito> creditos = Arrays.asList(creditoMock);
        List<CreditoResponseDTO> creditosResponse = Arrays.asList(creditoResponseMock);

        when(creditoRepository.findAll()).thenReturn(creditos);
        when(creditoMapper.toResponseDTOList(creditos)).thenReturn(creditosResponse);

        // When
        List<CreditoResponseDTO> resultado = creditoService.listarTodosCreditos();

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);

        verify(creditoRepository).findAll();
        verify(creditoMapper).toResponseDTOList(creditos);
    }

    @Test
    @DisplayName("Deve buscar créditos por tipo com sucesso")
    void deveBuscarCreditosPorTipoComSucesso() {
        // Given
        String tipoCredito = "ISSQN";
        List<Credito> creditos = Arrays.asList(creditoMock);
        List<CreditoResponseDTO> creditosResponse = Arrays.asList(creditoResponseMock);

        when(creditoRepository.findByTipoCredito(tipoCredito)).thenReturn(creditos);
        when(creditoMapper.toResponseDTOList(creditos)).thenReturn(creditosResponse);

        // When
        List<CreditoResponseDTO> resultado = creditoService.buscarCreditosPorTipo(tipoCredito);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTipoCredito()).isEqualTo(tipoCredito);

        verify(creditoRepository).findByTipoCredito(tipoCredito);
        verify(creditoMapper).toResponseDTOList(creditos);
    }

    @Test
    @DisplayName("Deve verificar existência de crédito com sucesso")
    void deveVerificarExistenciaCreditoComSucesso() {
        // Given
        String numeroCredito = "123456";
        when(creditoRepository.existsByNumeroCredito(numeroCredito)).thenReturn(true);

        // When
        boolean resultado = creditoService.existeCreditoPorNumero(numeroCredito);

        // Then
        assertThat(resultado).isTrue();

        verify(creditoRepository).existsByNumeroCredito(numeroCredito);
    }

    @Test
    @DisplayName("Deve retornar false para crédito inexistente")
    void deveRetornarFalseParaCreditoInexistente() {
        // Given
        String numeroCredito = "999999";
        when(creditoRepository.existsByNumeroCredito(numeroCredito)).thenReturn(false);

        // When
        boolean resultado = creditoService.existeCreditoPorNumero(numeroCredito);

        // Then
        assertThat(resultado).isFalse();

        verify(creditoRepository).existsByNumeroCredito(numeroCredito);
    }

    @Test
    @DisplayName("Deve retornar false para número de crédito vazio")
    void deveRetornarFalseParaNumeroCreditoVazio() {
        // When
        boolean resultado = creditoService.existeCreditoPorNumero("");

        // Then
        assertThat(resultado).isFalse();

        verify(creditoRepository, never()).existsByNumeroCredito(anyString());
    }

    @Test
    @DisplayName("Deve buscar últimos créditos constituídos com sucesso")
    void deveBuscarUltimosCreditosConstituidosComSucesso() {
        // Given
        int limite = 5;
        List<Credito> creditos = Arrays.asList(creditoMock);
        List<CreditoResponseDTO> creditosResponse = Arrays.asList(creditoResponseMock);

        when(creditoRepository.findUltimosCreditosConstituidos(limite)).thenReturn(creditos);
        when(creditoMapper.toResponseDTOList(creditos)).thenReturn(creditosResponse);

        // When
        List<CreditoResponseDTO> resultado = creditoService.buscarUltimosCreditosConstituidos(limite);

        // Then
        assertThat(resultado).isNotEmpty();
        assertThat(resultado).hasSize(1);

        verify(creditoRepository).findUltimosCreditosConstituidos(limite);
        verify(creditoMapper).toResponseDTOList(creditos);
    }

    @Test
    @DisplayName("Deve lançar exceção para limite inválido")
    void deveLancarExcecaoParaLimiteInvalido() {
        // When & Then
        assertThatThrownBy(() -> creditoService.buscarUltimosCreditosConstituidos(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Limite deve ser maior que zero");

        assertThatThrownBy(() -> creditoService.buscarUltimosCreditosConstituidos(1001))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Limite não pode ser maior que 1000");

        verify(creditoRepository, never()).findUltimosCreditosConstituidos(anyInt());
    }
}

