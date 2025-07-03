package br.com.api.creditos.service;

import br.com.api.creditos.dto.CreditoMapper;
import br.com.api.creditos.dto.CreditoResponseDTO;
import br.com.api.creditos.entity.Credito;
import br.com.api.creditos.exception.CreditoNotFoundException;
import br.com.api.creditos.repository.CreditoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Implementação do serviço para operações de negócio relacionadas a créditos.
 * 
 * Contém a lógica de negócio para consulta de créditos constituídos,
 * incluindo validações, transformações e integração com o repositório.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CreditoServiceImpl implements CreditoService {

    private final CreditoRepository creditoRepository;
    private final CreditoMapper creditoMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CreditoResponseDTO> buscarCreditosPorNfse(String numeroNfse) {
        log.info("Buscando créditos para NFS-e: {}", numeroNfse);
        
        validarParametroNaoVazio(numeroNfse, "Número da NFS-e");
        
        List<Credito> creditos = creditoRepository.findByNumeroNfseOrderByDataConstituicaoDesc(numeroNfse);
        
        if (creditos.isEmpty()) {
            log.warn("Nenhum crédito encontrado para NFS-e: {}", numeroNfse);
            throw CreditoNotFoundException.porNumeroNfse(numeroNfse);
        }
        
        log.info("Encontrados {} crédito(s) para NFS-e: {}", creditos.size(), numeroNfse);
        return creditoMapper.toResponseDTOList(creditos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CreditoResponseDTO buscarCreditoPorNumero(String numeroCredito) {
        log.info("Buscando crédito por número: {}", numeroCredito);
        
        validarParametroNaoVazio(numeroCredito, "Número do crédito");
        
        Credito credito = creditoRepository.findByNumeroCredito(numeroCredito)
                .orElseThrow(() -> {
                    log.warn("Crédito não encontrado com número: {}", numeroCredito);
                    return CreditoNotFoundException.porNumeroCredito(numeroCredito);
                });
        
        log.info("Crédito encontrado: {} - NFS-e: {}", numeroCredito, credito.getNumeroNfse());
        return creditoMapper.toResponseDTO(credito);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CreditoResponseDTO> listarTodosCreditos() {
        log.info("Listando todos os créditos");
        
        List<Credito> creditos = creditoRepository.findAll();
        
        log.info("Total de créditos encontrados: {}", creditos.size());
        return creditoMapper.toResponseDTOList(creditos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CreditoResponseDTO> buscarCreditosPorTipo(String tipoCredito) {
        log.info("Buscando créditos por tipo: {}", tipoCredito);
        
        validarParametroNaoVazio(tipoCredito, "Tipo do crédito");
        
        List<Credito> creditos = creditoRepository.findByTipoCredito(tipoCredito);
        
        log.info("Encontrados {} crédito(s) do tipo: {}", creditos.size(), tipoCredito);
        return creditoMapper.toResponseDTOList(creditos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existeCreditoPorNumero(String numeroCredito) {
        log.debug("Verificando existência do crédito: {}", numeroCredito);
        
        if (!StringUtils.hasText(numeroCredito)) {
            return false;
        }
        
        boolean existe = creditoRepository.existsByNumeroCredito(numeroCredito);
        log.debug("Crédito {} {}", numeroCredito, existe ? "existe" : "não existe");
        
        return existe;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CreditoResponseDTO> buscarUltimosCreditosConstituidos(int limite) {
        log.info("Buscando últimos {} créditos constituídos", limite);
        
        validarLimite(limite);
        
        List<Credito> creditos = creditoRepository.findUltimosCreditosConstituidos(limite);
        
        log.info("Encontrados {} crédito(s) recentes", creditos.size());
        return creditoMapper.toResponseDTOList(creditos);
    }

    /**
     * Valida se um parâmetro não está vazio ou nulo.
     * 
     * @param parametro valor do parâmetro
     * @param nomeParametro nome do parâmetro para mensagem de erro
     * @throws IllegalArgumentException se o parâmetro for inválido
     */
    private void validarParametroNaoVazio(String parametro, String nomeParametro) {
        if (!StringUtils.hasText(parametro)) {
            String mensagem = String.format("%s não pode ser vazio ou nulo", nomeParametro);
            log.warn("Validação falhou: {}", mensagem);
            throw new IllegalArgumentException(mensagem);
        }
    }

    /**
     * Valida o limite para consultas.
     * 
     * @param limite valor do limite
     * @throws IllegalArgumentException se o limite for inválido
     */
    private void validarLimite(int limite) {
        if (limite <= 0) {
            String mensagem = "Limite deve ser maior que zero";
            log.warn("Validação falhou: {}", mensagem);
            throw new IllegalArgumentException(mensagem);
        }
        
        if (limite > 1000) {
            String mensagem = "Limite não pode ser maior que 1000";
            log.warn("Validação falhou: {}", mensagem);
            throw new IllegalArgumentException(mensagem);
        }
    }
}

