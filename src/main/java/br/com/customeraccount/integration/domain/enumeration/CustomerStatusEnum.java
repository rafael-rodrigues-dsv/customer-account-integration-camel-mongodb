package br.com.customeraccount.integration.domain.enumeration;

import lombok.Getter;

@Getter
public enum CustomerStatusEnum {
    IMPORTED("Importado"),
    READY_TO_PROCESS("Pronto para Processar"),
    PROCESSING("Em Processamento"),
    SUCCESSFUL_PROCESSING("Processamento Concluído com Sucesso"),
    PROCESSING_FAILURE("Falha no Processamento");

    private final String description;

    CustomerStatusEnum(String description) {
        this.description = description;
    }
}
