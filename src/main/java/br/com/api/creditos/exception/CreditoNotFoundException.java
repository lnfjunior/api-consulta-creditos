package br.com.api.creditos.exception;

/**
 * Exceção lançada quando um crédito não é encontrado.
 * 
 * Esta exceção é utilizada quando uma consulta por número do crédito
 * ou número da NFS-e não retorna resultados.
 * 
 * @author Luiz Nogueira
 * @version 1.0.0
 * @since 2025
 */
public class CreditoNotFoundException extends RuntimeException {

    /**
     * Construtor com mensagem.
     * 
     * @param message mensagem de erro
     */
    public CreditoNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa.
     * 
     * @param message mensagem de erro
     * @param cause causa da exceção
     */
    public CreditoNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Cria exceção para crédito não encontrado por número.
     * 
     * @param numeroCredito número do crédito
     * @return exceção configurada
     */
    public static CreditoNotFoundException porNumeroCredito(String numeroCredito) {
        return new CreditoNotFoundException(
            String.format("Crédito não encontrado com o número: %s", numeroCredito)
        );
    }

    /**
     * Cria exceção para nenhum crédito encontrado por NFS-e.
     * 
     * @param numeroNfse número da NFS-e
     * @return exceção configurada
     */
    public static CreditoNotFoundException porNumeroNfse(String numeroNfse) {
        return new CreditoNotFoundException(
            String.format("Nenhum crédito encontrado para a NFS-e: %s", numeroNfse)
        );
    }
}

