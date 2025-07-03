package br.com.api.creditos.config;

import br.com.api.creditos.messaging.AuditoriaService;
import br.com.api.creditos.messaging.event.ConsultaCreditoEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor para auditoria automática de consultas.
 * 
 * Captura automaticamente todas as requisições para endpoints de consulta
 * e envia eventos de auditoria para o Kafka.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditoriaInterceptor implements HandlerInterceptor {

    private final AuditoriaService auditoriaService;
    
    private static final String START_TIME_ATTRIBUTE = "startTime";
    private static final String TIPO_CONSULTA_ATTRIBUTE = "tipoConsulta";
    private static final String PARAMETRO_CONSULTA_ATTRIBUTE = "parametroConsulta";

    /**
     * Executado antes do processamento da requisição.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Marca o tempo de início
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // Identifica o tipo de consulta baseado na URL
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/creditos")) {
            ConsultaCreditoEvent.TipoConsulta tipoConsulta = identificarTipoConsulta(uri, request);
            String parametroConsulta = extrairParametroConsulta(uri, request);
            
            request.setAttribute(TIPO_CONSULTA_ATTRIBUTE, tipoConsulta);
            request.setAttribute(PARAMETRO_CONSULTA_ATTRIBUTE, parametroConsulta);
            
            log.debug("Iniciando auditoria para consulta: {} - Parâmetro: {}", 
                     tipoConsulta, parametroConsulta);
        }
        
        return true;
    }

    /**
     * Executado após o processamento bem-sucedido da requisição.
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, 
                          Object handler, ModelAndView modelAndView) {
        // Implementação vazia - processamento principal no afterCompletion
    }

    /**
     * Executado após a conclusão da requisição (sucesso ou erro).
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        
        ConsultaCreditoEvent.TipoConsulta tipoConsulta = 
            (ConsultaCreditoEvent.TipoConsulta) request.getAttribute(TIPO_CONSULTA_ATTRIBUTE);
        
        if (tipoConsulta != null) {
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            Long tempoExecucao = startTime != null ? System.currentTimeMillis() - startTime : 0L;
            
            String parametroConsulta = (String) request.getAttribute(PARAMETRO_CONSULTA_ATTRIBUTE);
            String enderecoIp = obterEnderecoIp(request);
            String userAgent = request.getHeader("User-Agent");
            
            try {
                if (ex != null || response.getStatus() >= 400) {
                    // Consulta com erro
                    String mensagemErro = ex != null ? ex.getMessage() : 
                                        "HTTP " + response.getStatus();
                    
                    auditoriaService.publicarConsultaErro(
                        tipoConsulta, parametroConsulta, mensagemErro, 
                        tempoExecucao, enderecoIp, userAgent
                    );
                } else {
                    // Consulta bem-sucedida
                    Integer quantidadeResultados = extrairQuantidadeResultados(response);
                    
                    auditoriaService.publicarConsultaSucesso(
                        tipoConsulta, parametroConsulta, quantidadeResultados,
                        tempoExecucao, enderecoIp, userAgent
                    );
                }
                
                log.debug("Auditoria concluída para consulta: {} - Tempo: {}ms", 
                         tipoConsulta, tempoExecucao);
                
            } catch (Exception e) {
                log.error("Erro ao processar auditoria: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * Identifica o tipo de consulta baseado na URI.
     */
    private ConsultaCreditoEvent.TipoConsulta identificarTipoConsulta(String uri, HttpServletRequest request) {
        if (uri.matches("/api/creditos/\\d+")) {
            return ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_NFSE;
        } else if (uri.matches("/api/creditos/credito/[^/]+")) {
            return ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_NUMERO_CREDITO;
        } else if (uri.equals("/api/creditos") && "GET".equals(request.getMethod())) {
            return ConsultaCreditoEvent.TipoConsulta.LISTAR_TODOS;
        } else if (uri.matches("/api/creditos/tipo/[^/]+")) {
            return ConsultaCreditoEvent.TipoConsulta.CONSULTA_POR_TIPO;
        } else if (uri.equals("/api/creditos/recentes")) {
            return ConsultaCreditoEvent.TipoConsulta.CONSULTA_RECENTES;
        } else if (uri.matches("/api/creditos/credito/[^/]+/existe")) {
            return ConsultaCreditoEvent.TipoConsulta.VERIFICAR_EXISTENCIA;
        }
        
        return ConsultaCreditoEvent.TipoConsulta.LISTAR_TODOS; // Padrão
    }

    /**
     * Extrai o parâmetro de consulta da URI.
     */
    private String extrairParametroConsulta(String uri, HttpServletRequest request) {
        String[] parts = uri.split("/");
        
        if (uri.matches("/api/creditos/\\d+")) {
            return parts[parts.length - 1]; // Número da NFS-e
        } else if (uri.matches("/api/creditos/credito/[^/]+")) {
            return parts[parts.length - 1]; // Número do crédito
        } else if (uri.matches("/api/creditos/tipo/[^/]+")) {
            return parts[parts.length - 1]; // Tipo do crédito
        } else if (uri.equals("/api/creditos/recentes")) {
            String limite = request.getParameter("limite");
            return limite != null ? "limite=" + limite : "limite=10";
        } else if (uri.matches("/api/creditos/credito/[^/]+/existe")) {
            return parts[parts.length - 2]; // Número do crédito
        }
        
        return "N/A";
    }

    /**
     * Obtém o endereço IP real do cliente.
     */
    private String obterEnderecoIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * Tenta extrair a quantidade de resultados da resposta.
     * Nota: Esta é uma implementação simplificada.
     */
    private Integer extrairQuantidadeResultados(HttpServletResponse response) {
        // Em uma implementação real, seria necessário interceptar o corpo da resposta
        // Por simplicidade, retornamos null aqui
        return null;
    }
}

