# API de Consulta de Créditos

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-3.5+-red.svg)](https://kafka.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 📋 Descrição

API RESTful desenvolvida em Java 21 com Spring Boot para consulta de créditos constituídos. A aplicação fornece endpoints para consulta de créditos por número da NFS-e ou por número do crédito, incluindo informações fiscais detalhadas como valor do ISSQN, tipo do crédito, alíquota e outros dados relevantes.

### 🎯 Funcionalidades Principais

- ✅ **Consulta por NFS-e**: Busca todos os créditos relacionados a uma NFS-e específica
- ✅ **Consulta por Número do Crédito**: Busca um crédito específico pelo seu número
- ✅ **Listagem Completa**: Lista todos os créditos cadastrados no sistema
- ✅ **Filtros Avançados**: Busca por tipo de crédito e créditos recentes
- ✅ **Verificação de Existência**: Verifica se um crédito existe no sistema
- ✅ **Auditoria Automática**: Integração com Apache Kafka para auditoria de consultas
- ✅ **Documentação Interativa**: Swagger UI para teste e documentação da API
- ✅ **Testes Abrangentes**: Cobertura de testes unitários e de integração

### 🏗️ Arquitetura

A aplicação segue os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**, com separação clara de responsabilidades:

```
src/
├── main/java/br/com/api/creditos/
│   ├── config/           # Configurações (CORS, OpenAPI, Kafka, etc.)
│   ├── controller/       # Controllers REST
│   ├── dto/              # Data Transfer Objects e Mappers
│   ├── entity/           # Entidades JPA
│   ├── exception/        # Exceções customizadas e handlers
│   ├── messaging/        # Integração com Kafka
│   ├── repository/       # Repositories JPA
│   └── service/          # Lógica de negócio
└── test/                 # Testes unitários e de integração
```

## 🚀 Tecnologias Utilizadas

### Core
- **Java 21** - Linguagem de programação com recursos modernos
- **Spring Boot 3.2.0** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Web** - APIs REST
- **Spring Validation** - Validação de dados

### Banco de Dados
- **PostgreSQL 15+** - Banco de dados principal
- **Flyway** - Migração de banco de dados
- **HikariCP** - Pool de conexões

### Messaging
- **Apache Kafka** - Sistema de mensageria para auditoria
- **Spring Kafka** - Integração Spring com Kafka

### Documentação
- **SpringDoc OpenAPI 3** - Documentação automática da API
- **Swagger UI** - Interface interativa para testes

### Utilitários
- **Lombok** - Redução de boilerplate
- **MapStruct** - Mapeamento entre objetos
- **Jackson** - Serialização JSON

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks para testes unitários
- **AssertJ** - Assertions fluentes
- **Spring Boot Test** - Testes de integração
- **Testcontainers** - Testes com containers

## 📦 Instalação e Configuração

### Pré-requisitos

- Java 21 ou superior
- Maven 3.8+
- PostgreSQL 15+
- Apache Kafka 3.5+ (opcional, para auditoria)
- Docker e Docker Compose (opcional)

### 1. Clone do Repositório

```bash
git clone <repository-url>
cd api-consulta-creditos
```

### 2. Configuração do Banco de Dados

#### Opção A: PostgreSQL Local

```sql
-- Criar banco de dados
CREATE DATABASE creditos_db;
CREATE USER creditos_user WITH PASSWORD 'creditos_pass';
GRANT ALL PRIVILEGES ON DATABASE creditos_db TO creditos_user;
```

#### Opção B: Docker Compose

```bash
# Subir PostgreSQL e Kafka via Docker
docker-compose up -d postgres kafka
```

### 3. Configuração das Variáveis de Ambiente

Crie um arquivo `.env` ou configure as variáveis:

```bash
# Banco de Dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=creditos_db
DB_USERNAME=creditos_user
DB_PASSWORD=creditos_pass

# Kafka (opcional)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Aplicação
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

### 4. Compilação e Execução

```bash
# Compilar o projeto
mvn clean compile

# Executar testes
mvn test

# Executar a aplicação
mvn spring-boot:run

# Ou gerar JAR e executar
mvn clean package
java -jar target/api-consulta-creditos-1.0.0.jar
```

## 🔧 Configuração

### Perfis de Ambiente

A aplicação suporta diferentes perfis:

- **dev** - Desenvolvimento local
- **test** - Testes automatizados
- **prod** - Produção

### Configurações Principais

```yaml
# application.yml
spring:
  profiles:
    active: dev
  
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:creditos_db}
    username: ${DB_USERNAME:creditos_user}
    password: ${DB_PASSWORD:creditos_pass}
  
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}

server:
  port: ${SERVER_PORT:8080}

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:4200}
  kafka:
    topics:
      consulta-credito: ${KAFKA_TOPIC_CONSULTA:consulta-credito-topic}
```

## 📚 Uso da API

### Endpoints Principais

#### 1. Buscar Créditos por NFS-e
```http
GET /api/creditos/{numeroNfse}
```

**Exemplo:**
```bash
curl -X GET "http://localhost:8080/api/creditos/7891011" \
     -H "Accept: application/json"
```

#### 2. Buscar Crédito por Número
```http
GET /api/creditos/credito/{numeroCredito}
```

**Exemplo:**
```bash
curl -X GET "http://localhost:8080/api/creditos/credito/123456" \
     -H "Accept: application/json"
```

#### 3. Listar Todos os Créditos
```http
GET /api/creditos
```

#### 4. Buscar por Tipo
```http
GET /api/creditos/tipo/{tipo}
```

#### 5. Créditos Recentes
```http
GET /api/creditos/recentes?limite=10
```

#### 6. Verificar Existência
```http
GET /api/creditos/credito/{numeroCredito}/existe
```

### Formato de Resposta

```json
{
  "numeroCredito": "123456",
  "numeroNfse": "7891011",
  "dataConstituicao": "2024-02-25",
  "valorIssqn": 1500.75,
  "tipoCredito": "ISSQN",
  "simplesNacional": "Sim",
  "aliquota": 5.0,
  "valorFaturado": 30000.00,
  "valorDeducao": 5000.00,
  "baseCalculo": 25000.00
}
```

### Tratamento de Erros

```json
{
  "timestamp": "2024-02-25T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Crédito não encontrado com o número: 999999",
  "path": "/api/creditos/credito/999999"
}
```

## 📖 Documentação da API

### Swagger UI

Acesse a documentação interativa em:
- **Desenvolvimento**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Health Checks

```http
GET /api/health    # Status da aplicação
GET /api/info      # Informações detalhadas
GET /api/ping      # Conectividade básica
```

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Testes por Categoria

```bash
# Apenas testes unitários
mvn test -Dtest="**/*Test"

# Apenas testes de integração
mvn test -Dtest="**/*IntegrationTest"

# Cobertura de código
mvn jacoco:report
```

### Estrutura de Testes

- **Unitários**: Testam componentes isoladamente
- **Integração**: Testam fluxos completos com banco de dados
- **Contratos**: Validam contratos da API

## 🔍 Monitoramento e Auditoria

### Kafka Integration

A aplicação publica eventos de auditoria no Kafka para cada consulta realizada:

```json
{
  "eventId": "uuid-v4",
  "timestamp": "2024-02-25T10:30:00",
  "tipoConsulta": "CONSULTA_POR_NFSE",
  "parametroConsulta": "7891011",
  "quantidadeResultados": 2,
  "enderecoIp": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "tempoExecucaoMs": 150,
  "sucesso": true
}
```

### Logs Estruturados

A aplicação utiliza logging estruturado com níveis apropriados:

```bash
# Configurar nível de log
export LOGGING_LEVEL_BR_COM_API_CREDITOS=DEBUG
```

## 🚀 Deploy

### Docker

```dockerfile
# Dockerfile incluído no projeto
docker build -t api-consulta-creditos .
docker run -p 8080:8080 api-consulta-creditos
```

### Docker Compose

```bash
# Subir toda a stack
docker-compose up -d

# Verificar status
docker-compose ps

# Logs
docker-compose logs -f api
```

### Kubernetes

Manifests Kubernetes disponíveis em `/k8s/`:

```bash
kubectl apply -f k8s/
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

### Padrões de Código

- Seguir convenções do Google Java Style Guide
- Cobertura de testes mínima: 80%
- Documentação obrigatória para APIs públicas
- Commits semânticos (Conventional Commits)

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👥 Equipe

- **Luiz Nogueira** - Desenvolvimento inicial e manutenção

## 📞 Suporte

Para suporte e dúvidas:

- 📧 Email: lnfjunior@gmail.com

---

**Desenvolvido com ❤️ por Luiz Nogueira usando Java 21 e Spring Boot**

