package br.com.api.creditos.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração web da aplicação.
 *
 * Define configurações relacionadas ao comportamento web da aplicação,
 * incluindo CORS para permitir acesso do front-end Angular e
 * interceptors para auditoria automática.
 *
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    private final AuditoriaInterceptor auditoriaInterceptor;

    @Value("${app.cors.allowed-origins:http://localhost:4200}")
    private String[] allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    /**
     * Configura CORS para permitir acesso do front-end.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Configurando CORS - Origins permitidas: {}", (Object) allowedOrigins);

        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods(allowedMethods)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials)
                .maxAge(3600);

        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);

        registry.addMapping("/api-docs/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    /**
     * Registra interceptors da aplicação.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Registrando interceptor de auditoria");

        registry.addInterceptor(auditoriaInterceptor)
                .addPathPatterns("/api/creditos/**")
                .excludePathPatterns(
                        "/api/health",
                        "/api/info",
                        "/api/ping",
                        "/swagger-ui/**",
                        "/api-docs/**"
                );
    }
}

