package br.com.api.creditos.messaging;

import br.com.api.creditos.messaging.event.ConsultaCreditoEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AuditoriaService.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuditoriaService - Testes Unitários")
class AuditoriaServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private AuditoriaService auditoriaService;

    private final String topicName = "consulta-credito-topic";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(auditoriaService, "consultaCreditoTopic", topicName);
    }

    @Test
    @DisplayName("Deve publicar evento de consulta com sucesso")
    void devePublicarEventoConsultaComSucesso() {
        // Given
        ConsultaCreditoEvent evento = ConsultaCreditoEvent.sucesso(
            ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_NFSE,
            "7891011",
            2,
            150L,
            "192.168.1.1",
            "Mozilla/5.0"
        );

        @SuppressWarnings("unchecked")
        CompletableFuture<SendResult<String, Object>> future = mock(CompletableFuture.class);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        auditoriaService.publicarEventoConsulta(evento);

        // Then
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo(topicName);
        assertThat(keyCaptor.getValue()).isEqualTo(evento.getEventId());
        assertThat(valueCaptor.getValue()).isEqualTo(evento);
    }

    @Test
    @DisplayName("Deve publicar evento de consulta bem-sucedida")
    void devePublicarEventoConsultaBemSucedida() {
        // Given
        @SuppressWarnings("unchecked")
        CompletableFuture<SendResult<String, Object>> future = mock(CompletableFuture.class);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        auditoriaService.publicarConsultaSucesso(
            ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_NUMERO_CREDITO,
            "123456",
            1,
            200L,
            "192.168.1.1",
            "Mozilla/5.0"
        );

        // Then
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(anyString(), anyString(), eventCaptor.capture());

        ConsultaCreditoEvent evento = (ConsultaCreditoEvent) eventCaptor.getValue();
        assertThat(evento.getTipoConsulta()).isEqualTo(ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_NUMERO_CREDITO);
        assertThat(evento.getParametroConsulta()).isEqualTo("123456");
        assertThat(evento.getQuantidadeResultados()).isEqualTo(1);
        assertThat(evento.getTempoExecucaoMs()).isEqualTo(200L);
        assertThat(evento.getSucesso()).isTrue();
        assertThat(evento.getMensagemErro()).isNull();
    }

    @Test
    @DisplayName("Deve publicar evento de consulta com erro")
    void devePublicarEventoConsultaComErro() {
        // Given
        @SuppressWarnings("unchecked")
        CompletableFuture<SendResult<String, Object>> future = mock(CompletableFuture.class);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);

        // When
        auditoriaService.publicarConsultaErro(
            ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_NFSE,
            "999999",
            "Crédito não encontrado",
            100L,
            "192.168.1.1",
            "Mozilla/5.0"
        );

        // Then
        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(anyString(), anyString(), eventCaptor.capture());

        ConsultaCreditoEvent evento = (ConsultaCreditoEvent) eventCaptor.getValue();
        assertThat(evento.getTipoConsulta()).isEqualTo(ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_NFSE);
        assertThat(evento.getParametroConsulta()).isEqualTo("999999");
        assertThat(evento.getQuantidadeResultados()).isEqualTo(0);
        assertThat(evento.getTempoExecucaoMs()).isEqualTo(100L);
        assertThat(evento.getSucesso()).isFalse();
        assertThat(evento.getMensagemErro()).isEqualTo("Crédito não encontrado");
    }

    @Test
    @DisplayName("Deve tratar exceção ao publicar evento")
    void deveTratarExcecaoAoPublicarEvento() {
        // Given
        ConsultaCreditoEvent evento = ConsultaCreditoEvent.sucesso(
            ConsultaCreditoEvent.TipoConsulta.LISTAR_TODOS,
            "N/A",
            5,
            300L,
            "192.168.1.1",
            "Mozilla/5.0"
        );

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Erro de conexão com Kafka"));

        // When & Then
        assertThatCode(() -> auditoriaService.publicarEventoConsulta(evento))
                .doesNotThrowAnyException();

        verify(kafkaTemplate).send(anyString(), anyString(), any());
    }

    @Test
    @DisplayName("Deve verificar se o serviço está saudável")
    void deveVerificarSeServicoEstaSaudavel() {
        // When
        boolean healthy = auditoriaService.isHealthy();

        // Then
        assertThat(healthy).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false quando KafkaTemplate for null")
    void deveRetornarFalseQuandoKafkaTemplateForNull() {
        // Given
        ReflectionTestUtils.setField(auditoriaService, "kafkaTemplate", null);

        // When
        boolean healthy = auditoriaService.isHealthy();

        // Then
        assertThat(healthy).isFalse();
    }

    @Test
    @DisplayName("Deve retornar status do serviço")
    void deveRetornarStatusDoServico() {
        // When
        String status = auditoriaService.getStatus();

        // Then
        assertThat(status).contains("AuditoriaService");
        assertThat(status).contains(topicName);
        assertThat(status).contains("Healthy: true");
    }

    @Test
    @DisplayName("Deve criar evento de sucesso corretamente")
    void deveCriarEventoSucessoCorretamente() {
        // When
        ConsultaCreditoEvent evento = ConsultaCreditoEvent.sucesso(
            ConsultaCreditoEvent.TipoConsulta.CONSULTA_RECENTES,
            "limite=10",
            10,
            250L,
            "10.0.0.1",
            "Chrome/91.0"
        );

        // Then
        assertThat(evento.getEventId()).isNotNull();
        assertThat(evento.getTimestamp()).isNotNull();
        assertThat(evento.getTipoConsulta()).isEqualTo(ConsultaCreditoEvent.TipoConsulta.CONSULTA_RECENTES);
        assertThat(evento.getParametroConsulta()).isEqualTo("limite=10");
        assertThat(evento.getQuantidadeResultados()).isEqualTo(10);
        assertThat(evento.getTempoExecucaoMs()).isEqualTo(250L);
        assertThat(evento.getEnderecoIp()).isEqualTo("10.0.0.1");
        assertThat(evento.getUserAgent()).isEqualTo("Chrome/91.0");
        assertThat(evento.getSucesso()).isTrue();
        assertThat(evento.getMensagemErro()).isNull();
    }

    @Test
    @DisplayName("Deve criar evento de erro corretamente")
    void deveCriarEventoErroCorretamente() {
        // When
        ConsultaCreditoEvent evento = ConsultaCreditoEvent.erro(
            ConsultaCreditoEvent.TipoConsulta.VERIFICAR_EXISTENCIA,
            "999999",
            "Parâmetro inválido",
            50L,
            "172.16.0.1",
            "Safari/14.0"
        );

        // Then
        assertThat(evento.getEventId()).isNotNull();
        assertThat(evento.getTimestamp()).isNotNull();
        assertThat(evento.getTipoConsulta()).isEqualTo(ConsultaCreditoEvent.TipoConsulta.VERIFICAR_EXISTENCIA);
        assertThat(evento.getParametroConsulta()).isEqualTo("999999");
        assertThat(evento.getQuantidadeResultados()).isEqualTo(0);
        assertThat(evento.getTempoExecucaoMs()).isEqualTo(50L);
        assertThat(evento.getEnderecoIp()).isEqualTo("172.16.0.1");
        assertThat(evento.getUserAgent()).isEqualTo("Safari/14.0");
        assertThat(evento.getSucesso()).isFalse();
        assertThat(evento.getMensagemErro()).isEqualTo("Parâmetro inválido");
    }
}

