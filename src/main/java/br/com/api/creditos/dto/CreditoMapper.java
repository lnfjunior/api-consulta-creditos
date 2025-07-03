package br.com.api.creditos.dto;

import br.com.api.creditos.entity.Credito;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper para conversão entre entidade Credito e DTOs.
 * 
 * Utiliza MapStruct para gerar automaticamente implementações
 * eficientes e type-safe de conversão entre objetos.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CreditoMapper {

    /**
     * Converte uma entidade Credito para CreditoResponseDTO.
     * 
     * @param credito entidade a ser convertida
     * @return DTO de resposta
     */
    @Mapping(target = "simplesNacional", expression = "java(formatSimplesNacional(credito.getSimplesNacional()))")
    CreditoResponseDTO toResponseDTO(Credito credito);

    /**
     * Converte uma lista de entidades Credito para lista de CreditoResponseDTO.
     * 
     * @param creditos lista de entidades
     * @return lista de DTOs de resposta
     */
    List<CreditoResponseDTO> toResponseDTOList(List<Credito> creditos);

    /**
     * Converte CreditoResponseDTO para entidade Credito.
     * 
     * @param dto DTO a ser convertido
     * @return entidade Credito
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "simplesNacional", expression = "java(parseSimplesNacional(dto.getSimplesNacional()))")
    Credito toEntity(CreditoResponseDTO dto);

    /**
     * Formata o valor booleano do Simples Nacional para string.
     * 
     * @param simplesNacional valor booleano
     * @return "Sim" se true, "Não" se false
     */
    default String formatSimplesNacional(Boolean simplesNacional) {
        return CreditoResponseDTO.formatSimplesNacional(simplesNacional);
    }

    /**
     * Converte a string do Simples Nacional para valor booleano.
     * 
     * @param simplesNacional string ("Sim" ou "Não")
     * @return true se "Sim", false caso contrário
     */
    default Boolean parseSimplesNacional(String simplesNacional) {
        return CreditoResponseDTO.parseSimplesNacional(simplesNacional);
    }
}

