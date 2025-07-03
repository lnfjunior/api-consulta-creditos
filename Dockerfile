# Multi-stage build para otimizar o tamanho da imagem

# Stage 1: Build da aplicação
FROM eclipse-temurin:21-jdk-alpine AS builder

# Instalar Maven
RUN apk add --no-cache maven

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos de configuração do Maven
COPY pom.xml .
COPY src ./src

# Build da aplicação (pular testes para build mais rápido)
RUN mvn clean package -DskipTests

# Stage 2: Runtime da aplicação
FROM eclipse-temurin:21-jre-alpine AS runtime

# Instalar curl para health checks
RUN apk add --no-cache curl

# Criar usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Definir diretório de trabalho
WORKDIR /app

# Copiar JAR da aplicação do stage anterior
COPY --from=builder /app/target/api-consulta-creditos-*.jar app.jar

# Criar diretórios necessários
RUN mkdir -p /app/logs && \
    chown -R appuser:appgroup /app

# Mudar para usuário não-root
USER appuser

# Expor porta da aplicação
EXPOSE 8080

# Configurar JVM para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.backgroundpreinitializer.ignore=true"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels para metadados
LABEL maintainer="Luiz Nogueira <lnfjunior@gmail.com>"
LABEL version="1.0.0"
LABEL description="API de Consulta de Créditos - Java 21 + Spring Boot"
LABEL org.opencontainers.image.source="https://github.com/empresa/api-consulta-creditos"
LABEL org.opencontainers.image.documentation="https://github.com/empresa/api-consulta-creditos/blob/main/README.md"

