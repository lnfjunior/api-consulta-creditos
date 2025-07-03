package br.com.api.creditos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidade que representa um crédito constituído no sistema.
 * 
 * Esta entidade mapeia a tabela 'credito' no banco de dados e contém
 * todas as informações necessárias sobre créditos constituídos,
 * incluindo dados da NFS-e, valores e configurações fiscais.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
@Entity
@Table(name = "credito", indexes = {
    @Index(name = "idx_credito_numero_credito", columnList = "numero_credito"),
    @Index(name = "idx_credito_numero_nfse", columnList = "numero_nfse"),
    @Index(name = "idx_credito_data_constituicao", columnList = "data_constituicao"),
    @Index(name = "idx_credito_tipo_credito", columnList = "tipo_credito")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"id"})
public class Credito {

    /**
     * Identificador único do crédito (chave primária).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Número identificador único do crédito constituído.
     */
    @Column(name = "numero_credito", nullable = false, length = 50)
    @NotBlank(message = "Número do crédito é obrigatório")
    @Size(max = 50, message = "Número do crédito deve ter no máximo 50 caracteres")
    private String numeroCredito;

    /**
     * Número da Nota Fiscal de Serviços Eletrônica.
     */
    @Column(name = "numero_nfse", nullable = false, length = 50)
    @NotBlank(message = "Número da NFS-e é obrigatório")
    @Size(max = 50, message = "Número da NFS-e deve ter no máximo 50 caracteres")
    private String numeroNfse;

    /**
     * Data de constituição do crédito.
     */
    @Column(name = "data_constituicao", nullable = false)
    @NotNull(message = "Data de constituição é obrigatória")
    @PastOrPresent(message = "Data de constituição não pode ser futura")
    private LocalDate dataConstituicao;

    /**
     * Valor do Imposto Sobre Serviços de Qualquer Natureza.
     */
    @Column(name = "valor_issqn", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor do ISSQN é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor do ISSQN deve ser positivo")
    @Digits(integer = 13, fraction = 2, message = "Valor do ISSQN deve ter no máximo 13 dígitos inteiros e 2 decimais")
    private BigDecimal valorIssqn;

    /**
     * Tipo do crédito (ISSQN, Outros, etc.).
     */
    @Column(name = "tipo_credito", nullable = false, length = 50)
    @NotBlank(message = "Tipo do crédito é obrigatório")
    @Size(max = 50, message = "Tipo do crédito deve ter no máximo 50 caracteres")
    private String tipoCredito;

    /**
     * Indica se é optante pelo Simples Nacional.
     */
    @Column(name = "simples_nacional", nullable = false)
    @NotNull(message = "Indicação do Simples Nacional é obrigatória")
    private Boolean simplesNacional;

    /**
     * Alíquota aplicada no cálculo do imposto.
     */
    @Column(name = "aliquota", nullable = false, precision = 5, scale = 2)
    @NotNull(message = "Alíquota é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Alíquota deve ser positiva")
    @DecimalMax(value = "100.0", message = "Alíquota não pode ser superior a 100%")
    @Digits(integer = 3, fraction = 2, message = "Alíquota deve ter no máximo 3 dígitos inteiros e 2 decimais")
    private BigDecimal aliquota;

    /**
     * Valor total faturado.
     */
    @Column(name = "valor_faturado", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor faturado é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Valor faturado deve ser positivo")
    @Digits(integer = 13, fraction = 2, message = "Valor faturado deve ter no máximo 13 dígitos inteiros e 2 decimais")
    private BigDecimal valorFaturado;

    /**
     * Valor das deduções aplicadas.
     */
    @Column(name = "valor_deducao", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Valor de dedução é obrigatório")
    @DecimalMin(value = "0.0", message = "Valor de dedução não pode ser negativo")
    @Digits(integer = 13, fraction = 2, message = "Valor de dedução deve ter no máximo 13 dígitos inteiros e 2 decimais")
    private BigDecimal valorDeducao;

    /**
     * Base de cálculo para o imposto.
     */
    @Column(name = "base_calculo", nullable = false, precision = 15, scale = 2)
    @NotNull(message = "Base de cálculo é obrigatória")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base de cálculo deve ser positiva")
    @Digits(integer = 13, fraction = 2, message = "Base de cálculo deve ter no máximo 13 dígitos inteiros e 2 decimais")
    private BigDecimal baseCalculo;

    /**
     * Data e hora de criação do registro.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última atualização do registro.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Implementação customizada do equals baseada no número do crédito.
     * 
     * @param o objeto a ser comparado
     * @return true se os objetos são iguais, false caso contrário
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Credito credito = (Credito) o;
        return Objects.equals(numeroCredito, credito.numeroCredito);
    }

    /**
     * Implementação customizada do hashCode baseada no número do crédito.
     * 
     * @return hash code do objeto
     */
    @Override
    public int hashCode() {
        return Objects.hash(numeroCredito);
    }

    /**
     * Verifica se o crédito é do tipo ISSQN.
     * 
     * @return true se for ISSQN, false caso contrário
     */
    public boolean isIssqn() {
        return "ISSQN".equalsIgnoreCase(this.tipoCredito);
    }

    /**
     * Calcula o valor líquido do crédito (valor faturado - valor dedução).
     * 
     * @return valor líquido calculado
     */
    public BigDecimal calcularValorLiquido() {
        return this.valorFaturado.subtract(this.valorDeducao);
    }

    /**
     * Verifica se os valores estão consistentes (base de cálculo = valor faturado - valor dedução).
     * 
     * @return true se os valores estão consistentes, false caso contrário
     */
    public boolean isValoresConsistentes() {
        BigDecimal valorLiquido = calcularValorLiquido();
        return this.baseCalculo.compareTo(valorLiquido) == 0;
    }
}

