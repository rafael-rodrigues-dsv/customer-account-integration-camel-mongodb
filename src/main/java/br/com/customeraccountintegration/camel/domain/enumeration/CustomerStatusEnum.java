package br.com.customeraccountintegration.camel.domain.enumeration;

import lombok.Getter;

@Getter
public enum CustomerStatusEnum {
  IMPORTED("Importado"),
  PROCESSING("Em Processamento"),
  SUCCESSFUL_PROCESSING("Processamento Concluído com Sucesso"),
  PROCESSING_FAILURE("Falha no Processamento");

  private final String description;

  CustomerStatusEnum(String description) {
    this.description = description;
  }
}
