package br.com.api.creditos.service;

import br.com.api.creditos.dto.CreditoResponseDTO;

import java.util.List;

/**
 * Interface do serviço para operações de negócio relacionadas a créditos.
 * 
 * Define os contratos para consulta de créditos constituídos,
 * implementando as regras de negócio da aplicação.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
public interface CreditoService {

    /**
     * Busca créditos pelo número da NFS-e.
     * 
     * @param numeroNfse número da NFS-e
     * @return lista de créditos encontrados
     * @throws br.com.api.creditos.exception.CreditoNotFoundException se nenhum crédito for encontrado
     */
    List<CreditoResponseDTO> buscarCreditosPorNfse(String numeroNfse);

    /**
     * Busca um crédito específico pelo número do crédito.
     * 
     * @param numeroCredito número do crédito
     * @return dados do crédito encontrado
     * @throws br.com.api.creditos.exception.CreditoNotFoundException se o crédito não for encontrado
     */
    CreditoResponseDTO buscarCreditoPorNumero(String numeroCredito);

    /**
     * Lista todos os créditos cadastrados.
     * 
     * @return lista de todos os créditos
     */
    List<CreditoResponseDTO> listarTodosCreditos();

    /**
     * Busca créditos por tipo.
     * 
     * @param tipoCredito tipo do crédito
     * @return lista de créditos do tipo especificado
     */
    List<CreditoResponseDTO> buscarCreditosPorTipo(String tipoCredito);

    /**
     * Verifica se um crédito existe pelo número.
     * 
     * @param numeroCredito número do crédito
     * @return true se existe, false caso contrário
     */
    boolean existeCreditoPorNumero(String numeroCredito);

    /**
     * Busca os últimos créditos constituídos.
     * 
     * @param limite quantidade máxima de registros
     * @return lista dos últimos créditos
     */
    List<CreditoResponseDTO> buscarUltimosCreditosConstituidos(int limite);
}

