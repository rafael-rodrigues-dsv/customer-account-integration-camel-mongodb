package br.com.customeraccountintegration.camel.domain.model;

import br.com.customeraccountintegration.camel.domain.enumeration.CustomerStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer")
public class CustomerModel {

  @Id
  private String id = UUID.randomUUID().toString();
  private String documentNumber;
  private CustomerStatusEnum customerStatus;
  private String email;
  private String contract;
  private LocalDateTime creationDate;
  private LocalDateTime updateDate;
  private String errorMessage;
}
