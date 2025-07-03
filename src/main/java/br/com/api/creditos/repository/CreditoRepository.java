package br.com.api.creditos.repository;

import br.com.api.creditos.entity.Credito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de acesso a dados da entidade Credito.
 * 
 * Fornece métodos para consulta de créditos constituídos no banco de dados,
 * incluindo consultas por número da NFS-e, número do crédito e outros filtros.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {

    /**
     * Busca créditos pelo número da NFS-e.
     * 
     * @param numeroNfse número da NFS-e
     * @return lista de créditos encontrados
     */
    List<Credito> findByNumeroNfse(String numeroNfse);

    /**
     * Busca créditos pelo número da NFS-e ordenados por data de constituição.
     * 
     * @param numeroNfse número da NFS-e
     * @return lista de créditos ordenados por data
     */
    List<Credito> findByNumeroNfseOrderByDataConstituicaoDesc(String numeroNfse);

    /**
     * Busca um crédito específico pelo número do crédito.
     * 
     * @param numeroCredito número do crédito
     * @return Optional contendo o crédito se encontrado
     */
    Optional<Credito> findByNumeroCredito(String numeroCredito);

    /**
     * Verifica se existe um crédito com o número informado.
     * 
     * @param numeroCredito número do crédito
     * @return true se existe, false caso contrário
     */
    boolean existsByNumeroCredito(String numeroCredito);

    /**
     * Busca créditos por tipo.
     * 
     * @param tipoCredito tipo do crédito
     * @return lista de créditos do tipo especificado
     */
    List<Credito> findByTipoCredito(String tipoCredito);

    /**
     * Busca créditos por período de constituição.
     * 
     * @param dataInicio data inicial
     * @param dataFim data final
     * @return lista de créditos no período
     */
    List<Credito> findByDataConstituicaoBetween(LocalDate dataInicio, LocalDate dataFim);

    /**
     * Busca créditos por optante do Simples Nacional.
     * 
     * @param simplesNacional true para optantes, false para não optantes
     * @return lista de créditos filtrados
     */
    List<Credito> findBySimplesNacional(Boolean simplesNacional);

    /**
     * Busca créditos com valor de ISSQN maior que o especificado.
     * 
     * @param valor valor mínimo
     * @return lista de créditos
     */
    @Query("SELECT c FROM Credito c WHERE c.valorIssqn > :valor ORDER BY c.valorIssqn DESC")
    List<Credito> findByValorIssqnGreaterThan(@Param("valor") java.math.BigDecimal valor);

    /**
     * Busca créditos por número da NFS-e e tipo.
     * 
     * @param numeroNfse número da NFS-e
     * @param tipoCredito tipo do crédito
     * @return lista de créditos
     */
    List<Credito> findByNumeroNfseAndTipoCredito(String numeroNfse, String tipoCredito);

    /**
     * Conta o número de créditos por tipo.
     * 
     * @param tipoCredito tipo do crédito
     * @return quantidade de créditos
     */
    long countByTipoCredito(String tipoCredito);

    /**
     * Busca créditos com consulta customizada para relatórios.
     * 
     * @param dataInicio data inicial
     * @param dataFim data final
     * @param tipoCredito tipo do crédito (opcional)
     * @return lista de créditos para relatório
     */
    @Query("""
        SELECT c FROM Credito c 
        WHERE c.dataConstituicao BETWEEN :dataInicio AND :dataFim
        AND (:tipoCredito IS NULL OR c.tipoCredito = :tipoCredito)
        ORDER BY c.dataConstituicao DESC, c.valorIssqn DESC
        """)
    List<Credito> findForRelatorio(
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim,
        @Param("tipoCredito") String tipoCredito
    );

    /**
     * Busca estatísticas agregadas por tipo de crédito.
     * 
     * @return lista de arrays com [tipoCredito, quantidade, somaValorIssqn]
     */
    @Query("""
        SELECT c.tipoCredito, COUNT(c), SUM(c.valorIssqn)
        FROM Credito c 
        GROUP BY c.tipoCredito
        ORDER BY COUNT(c) DESC
        """)
    List<Object[]> findEstatisticasPorTipo();

    /**
     * Busca créditos com valores inconsistentes (base de cálculo diferente de valor faturado - valor dedução).
     * 
     * @return lista de créditos com inconsistências
     */
    @Query("""
        SELECT c FROM Credito c 
        WHERE c.baseCalculo != (c.valorFaturado - c.valorDeducao)
        ORDER BY c.dataConstituicao DESC
        """)
    List<Credito> findCreditosComValoresInconsistentes();

    /**
     * Busca os últimos créditos constituídos.
     * 
     * @param limite quantidade máxima de registros
     * @return lista dos últimos créditos
     */
    @Query("""
        SELECT c FROM Credito c 
        ORDER BY c.dataConstituicao DESC, c.createdAt DESC
        LIMIT :limite
        """)
    List<Credito> findUltimosCreditosConstituidos(@Param("limite") int limite);
}

